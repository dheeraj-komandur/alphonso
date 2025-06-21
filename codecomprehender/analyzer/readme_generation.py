import os
import json
from llm import LLMClient
import re
from pydantic import BaseModel
import json
import base64
import re
import requests
from PIL import Image
import io
import yaml

# SYSTEM_PROMPT_README_GENERATION = '''
# # Expert README-Generation Prompt

# You are an expert software-architecture writer tasked with producing **one exhaustive `README.md`** for a large, production-grade Java repository.

# The user will supply **structured input**, one element per source file, containing only:

# * `path` – the file’s path relative to the repo root  
# * `description` – an enriched summary that already lists  
#   * public classes / interfaces  
#   * important methods & signatures  
#   * key outbound dependencies  

# Assume every fact you need lives inside `description`.

# ---

# ## Objectives (execute strictly in this order)

# 1. **Plan the README’s section hierarchy**  
#    * Before reviewing code structure, decide which **top-level sections, subsections, and sub-subsections** belong in a world-class README (e.g., Overview, Architecture, Component Breakdown, Workflows, Deployment, Contributing).  
#    * Think as a documentation author: what sequence best tells the project’s story?

# 2. **Infer code-base hierarchy and relationships** from `path` values and file-level descriptions, then map those findings into the planned sections.

# 3. **Design a multi-layer outline** (already conceived in step 1) and refine it using insights from step 2:  
#    * Start with an **eagle-eye architecture**.  
#    * Drill into progressively narrower sections mirroring logical groupings (packages, bounded contexts, workflows, etc.).

# 4. **Generate a click-able Table of Contents** immediately after the H1 title:  
#    * Bullet list of **internal GitHub-style anchors** (`[Text](#anchor)`), covering every heading you planned.  
#    * **No external URLs** may appear anywhere in the README.

# 5. **Populate each heading** with rich, developer-focused prose:  
#    * Describe *purpose, behaviour, collaborators, design patterns, constraints.*  
#    * Embed concise code snippets or function signatures where clarifying.  
#    * Use bullets or tables only when they aid scanning; otherwise favour paragraphs.  
#    * Be liberal with vocabulary and depth—assume readers want a thorough tour.

# 6. **Insert diagram placeholders** wherever a visual adds clarity.  
#    Use **exactly** this single-line tag (no additional markup):

# <!-- diagram: <TitleCamelCase> | <detailed description of what the diagram should depict, its scope, key actors, sequence, data flow, etc.> | files=<comma-separated paths> -->

# * `TitleCamelCase` — concise diagram name.  
# * **Description** — a *verbose* narrative of what the diagram shows and why it matters.  
# * `files=` — comma-separated list of every file_path whose behaviour the diagram represents (no spaces), the file paths should be exactly as they appear in the `file_path` field of the input. 
# * Use multiple diagrams if helpful—typically in high-level architecture sections, but also in fine-grained parts when warranted.


# 7. **Output only the finished README**—nothing else.  
# * First line **must** be `# <Repo Name>` (or `# Project Overview` if none supplied).  
# * Follow with a short, powerful project description.  
# * Then present the Table of Contents and the fully written sections.

# ---

# ### Strict omissions

# * **No external URLs** of any kind.  
# * **No TODOs, placeholders for missing data, or apologies.**  
# * **No output** other than the final Markdown README.
# '''


# SYSTEM_PROMPT_DIAGRAM_GENERATION = '''
# You are **DiagramSynthesizer**, an expert software architect and technical writer.
# Your job is to turn high-level code summaries into a clear, colourful Mermaid diagram.


# ## INPUT from the user
# - **title**  
# - **overview**  
# - **file_summaries** *(each line: `<path/File.ext>: <code file summary>`)*  

# ## OUTPUT  
# - **diagram_description** - a concise, third-person description of the diagram's purpose and scope (50-140 words).
# - **mermaid** - a Mermaid diagram in code block format

# ## TASK  

# 1. **Analyse context**  
#    - Read the *overview* to decide the most fitting diagram type (component map, class diagram, flow-chart, etc.).  
#    - Skim every entry in *file_summaries* to infer how files relate and which elements matter.  
#    - Identify key components, classes, or workflows that should appear in the diagram.

# 2. **Draft `diagram_description`**  
#    - 50-140 words, third-person, present tense.  
#    - Touch on the overview, then creatively expand with meaningful detail about layers, flows, or patterns the diagram will reveal.

# 3. **Render a stylish Mermaid diagram** (`mermaid`)  
#     - Pick a simple Mermaid flavour (e.g. `flowchart TD` or `classDiagram`).  
#     - Keep structure straightforward but apply pastel colours via `classDef` / `style` for visual grouping.  
#     - Use concise node labels, directional links, and minimal annotations; avoid overly complex subgraphs.

# ** Note - add quotes to labels in the mermaid code block.
# '''

class DiagramGenerator(BaseModel):
    discription: str  # concise, third-person description of the diagram's purpose and scope (50-140 words)
    mermaid: str  # Mermaid diagram in code block format

def get_system_prompt(yaml_filename):
    if os.path.exists(yaml_filename):
        try:
            with open(yaml_filename, 'r', encoding='utf-8') as yf:
                yaml_data = yaml.safe_load(yf)
                # print("YAML contents:", yaml_data)
                return yaml_data.get('system_prompt', '')
        except Exception as e:
            print(f"Failed to load YAML file {yaml_filename}: {e}")
            return ""
    return ""


def generate_readme_from_merged_analysis(directory: str, output_readme: str = "AI_README.md"):
    merged_json_path = os.path.join(directory, "merged_analysis.json")
    if not os.path.exists(merged_json_path):
        raise FileNotFoundError(f"{merged_json_path} not found.")
    with open(merged_json_path, 'r', encoding='utf-8') as f:
        merged_analysis = json.load(f)
    
    prompt = ""
    for java_file, analysis in merged_analysis.items():
        prompt += f"\n### {java_file}\n{analysis}\n"
    llm = LLMClient()

    get_system_prompt_path = os.path.join("codecomprehender", "analyzer", "prompts", "readme_generation.yaml")
    if not os.path.exists(get_system_prompt_path):
        raise FileNotFoundError(f"System prompt YAML file {get_system_prompt_path} not found.")
    system_prompt_readme_generation = get_system_prompt(get_system_prompt_path)
    readme_content = llm.reason_chat(system_prompt_readme_generation, prompt)
    with open(os.path.join(directory, output_readme), 'w', encoding='utf-8') as f:
        f.write(readme_content)
    print(f"README generated at {os.path.join(directory, output_readme)}")


def create_and_save_mermaid_diagram(graph: str, output_path: str, output_filename: str):
    """
    Generate a Mermaid diagram PNG using Kroki with default theme and save it.

    Args:
        graph: Mermaid diagram code (string).
        output_path: Directory to save the image.
        output_filename: Name of the PNG file.
    """

    # Add a default theme and font size via Mermaid init block
    init_block = (
        '%%{init: {"theme":"default", '
        '"themeVariables":{"background":"#ffffff","fontSize":"16px"}}}%%'
    )
    full_mermaid_code = f"{init_block}\n{graph}"

    url = "https://kroki.io/mermaid/png"
    headers = {"Content-Type": "application/json"}
    payload = {"diagram_source": full_mermaid_code}

    response = requests.post(url, json=payload, headers=headers, timeout=15)
    response.raise_for_status()

    os.makedirs(output_path, exist_ok=True)
    output_file = os.path.join(output_path, output_filename)
    with open(output_file, "wb") as f:
        f.write(response.content)

    # Open the saved PNG and ensure background is white (handles transparency)
    with Image.open(output_file) as img:
        if img.mode in ("RGBA", "LA") or (img.mode == "P" and "transparency" in img.info):
            bg = Image.new("RGBA", img.size, (255, 255, 255, 255))
            bg.paste(img, mask=img.split()[-1] if img.mode == "RGBA" else None)
            img = bg.convert("RGB")
            img.save(output_file, "PNG")
        elif img.mode != "RGB":
            img = img.convert("RGB")
            img.save(output_file, "PNG")
    print(f"Diagram saved as: {output_file}")


def process_diagram_tags(directory: str, readme_filename: str = "AI_README.md"):
    readme_path = os.path.join(directory, readme_filename)
    merged_json_path = os.path.join(directory, "merged_analysis.json")

    # Create AI_README_IMAGES folder if it doesn't exist
    images_dir = os.path.join(directory, "AI_README_IMAGES")
    os.makedirs(images_dir, exist_ok=True)

    if not os.path.exists(readme_path):
        raise FileNotFoundError(f"{readme_path} not found.")
    if not os.path.exists(merged_json_path):
        raise FileNotFoundError(f"{merged_json_path} not found.")

    with open(readme_path, 'r', encoding='utf-8') as f:
        readme_content = f.read()
    with open(merged_json_path, 'r', encoding='utf-8') as f:
        merged_analysis = json.load(f)

    # Regex to match diagram tags
    diagram_pattern = re.compile(
        r'<!-- diagram: ([^|]+)\|([^|]+)\| files=([^\s>]+) -->',
        re.IGNORECASE
    )
    diagrams = diagram_pattern.findall(readme_content)
    if not diagrams:
        print("No diagram tags found in README.")
        return
    
    
    llm = LLMClient()

    for idx, (title, description, files) in enumerate(diagrams):
        file_list = [f.strip() for f in files.split(',')]
        tag_str = f"<!-- diagram: {title}|{description}| files={files} -->"

        semantic_analyses = []
        for file_path in file_list:
            normalized_file_path = file_path.replace("/", os.sep).replace("\\", os.sep)
            found = False
            for analysis in merged_analysis.values():
                if "file_path" in analysis:
                    analysis_path = analysis["file_path"].replace("/", os.sep).replace("\\", os.sep)
                    if (
                        analysis_path.endswith(normalized_file_path)
                        or normalized_file_path.endswith(analysis_path)
                        or os.path.basename(analysis_path) == os.path.basename(normalized_file_path)
                    ):
                        if "structural_analysis" in analysis:
                            semantic_analyses.append(f"### {analysis['file_path']}\n{analysis['structural_analysis']}")
                            found = True
        semantic_context = "\n\n".join(semantic_analyses)

        user_prompt = (
            f"Title: {title.strip()}\n"
            f"Description: {description.strip()}\n"
            f"Files: {files.strip()}\n"
            f"Semantic Analysis:\n{semantic_context}\n"
        )

        max_retries = 4
        for attempt in range(max_retries):
            get_system_prompt_path_diagram = os.path.join("codecomprehender", "analyzer", "prompts", "readme_diagram_generator.yaml")
            if not os.path.exists(get_system_prompt_path_diagram):
                raise FileNotFoundError(f"System prompt YAML file {get_system_prompt_path_diagram} not found.")
            system_prompt_diagram_generator = get_system_prompt(get_system_prompt_path_diagram)
            diagram_details = llm.chat(system_prompt_diagram_generator, user_prompt, structured_format=DiagramGenerator)
            if isinstance(diagram_details, str):
                try:
                    diagram_details = json.loads(diagram_details)
                except Exception:
                    print("Failed to parse diagram_details as JSON.")
                    continue
                description = diagram_details.get('discription', '')
                mermaid = diagram_details.get('mermaid', '')
            else:
                description = getattr(diagram_details, 'discription', diagram_details)
                mermaid = getattr(diagram_details, 'mermaid', '')

            # print(f"\nDiagram: {title.strip()}\n")
            # print(f"Description:\n{description}\n")
            # print(f"Mermaid:\n{mermaid}\n")

            diagram_filename = f"{title.strip().replace(' ', '_').lower()}.png"
            try:
                create_and_save_mermaid_diagram(mermaid, images_dir, diagram_filename)
                image_markdown = f"![{title.strip()}](AI_README_IMAGES/{diagram_filename})\n\n{description.strip()}"
                readme_content = readme_content.replace(tag_str, image_markdown)
                with open(readme_path, 'w', encoding='utf-8') as f:
                    f.write(readme_content)
                break  # Success, exit retry loop
            except Exception as e:
                print(f"Failed to create or save diagram '{diagram_filename}': {e}")
                if attempt == max_retries - 1:
                    print(f"Giving up on diagram '{diagram_filename}' after {max_retries} attempts.")
                else:
                    print(f"Retrying diagram generation for '{diagram_filename}' (attempt {attempt + 2})...")
        # Save the updated README with images (in case of last attempt or success)
        with open(readme_path, 'w', encoding='utf-8') as f:
            f.write(readme_content)

# def main():
#     root_path = ".\examples\kitchensink-main"  # Change this as needed
#     generate_readme_from_merged_analysis(root_path)
#     process_diagram_tags(root_path)

# if __name__ == "__main__":
#     main()
