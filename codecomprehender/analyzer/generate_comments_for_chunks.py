import os
import json
from llm import LLMClient
from typing import List
from pydantic import BaseModel
import yaml

# SYSTEM_PROMPT = (
#     """
# You are an expert software engineer documenting an **entire source file**.
# The file is supplied with **line numbers** already annotated.

# Your response must be a **single JSON document** with exactly three top-level keys:

# * **`comments`** – an ordered list (top-to-bottom) of inline-comment objects.
# * **`structural_analysis`** – an in-depth narrative that captures the full structure and interconnections of the file.
# * **`file_summary`** – a brief summary of the file's purpose and key responsibilities.

# ### Fields inside each comment object

# * **`comment_code_range`** – inclusive span of source lines the comment describes.
#   *Example:* the whole class might be `"1-300"`, a method inside it might be `"40-80"`, and a `for`-loop inside that method might be `"50-70"`.
#   Ranges **may overlap**: the outer class range should wrap the inner method ranges, which in turn may wrap block ranges.

# * **`comment_linenumber`** – the line number (as a string) **above which** the comment should be inserted, e.g. `"39"`.

# * **`comment`** – It can be as long as necessary but must contain **no embedded newline characters**. Don't include comment delimiters like `//` or `/* */`—just the text itself.
#     The comment should be **succinct** but **complete**—it should stand alone as a clear explanation of the code it covers.
#     Length should scale with the size or complexity of the code span it covers.

# * **`comment_kind`** * – one of `class`, `interface`, `enum`, `record`, `method`, `function`, `constructor`, `loop`, `conditional`, `try`, `block`, or `other`.
#   This makes it easy to reconstruct the hierarchy programmatically.

# ---

# ## 📝 Part 1 – Generate Code Comments

# ### Hierarchical coverage

# 1. **Always produce nested / overlapping comments** where appropriate.
#    *Example:* a class comment covers `1-300`; each method inside still gets its own comment (`40-80`, `120-150`, etc.); large loops inside a method may get their own comments too (`50-70`).
# 2. Comments must be **ordered top-to-bottom** by `comment_linenumber`, even if that means an outer range appears before its inner ranges.

# ### Tone & Style

# * Clear, succinct, production quality—think *code reviewer*, not lecturer.
# * Exactly **one single-line comment** per construct; a post-processor will handle wrapping.

# ### High-Level Documentation (MANDATORY)

# Add a comment line immediately above every **class, interface, enum, record, or top-level function** describing:

# * Purpose and key responsibilities
# * Inputs, outputs, and notable side-effects
# * Critical invariants, design patterns, or annotations

# ### Block-Level Comments (CONDITIONAL)

# Add a comment line above any loop, conditional, try-catch, or algorithmic block when:

# * The block spans **10 or more source lines**, **or**
# * The logic is non-obvious or intricate.

# Skip trivial or self-evident blocks.

# ---

# ## 📘 Part 2 – Structural Analysis

# Write an in-depth narrative that lets another tool rebuild the call-graph, class diagram, and dependency map without seeing the code.

# 1. **File overview** – file name, primary role, design pattern or architectural context.
# 2. **Top-level types** – for each class/interface/enum/record:

#    * **Signature**: name, kind, super-class / implemented interfaces.
#    * **Role**: 1–2 sentences on responsibility.
#    * **Lifecycle notes**: singleton, immutable, thread-safe, etc.
# 3. **Members** –

#    * **Fields / properties**: name, type, visibility, modifiers, purpose.
#    * **Constructors / factories**: what they initialise or guarantee.
# 4. **Method deep-dive** (every non-trivial method):

#    * **Signature**: name, parameters, return type, annotations.
#    * **Behaviour**: actions, side-effects, exceptions.
#    * **Internal calls**: other methods in this file.
#    * **External calls**: key libraries or classes outside.
#    * **Key control flow**: notable loops, conditionals, error paths.
# 5. **Relationships** – invocations, overrides, listeners, injected dependencies, design-pattern links.
# 6. **External touchpoints** – public APIs, REST endpoints, external frameworks/services used.
# 7. **Algorithms / domain logic** – highlight important business rules or algorithms.
# 8. **Observations & caveats** – thread-safety, performance hotspots, TODO/FIXME markers, edge cases.

# ## 📘 Part 3 – File Summary 
# Write a brief summary of the file's purpose and key responsibilities, suitable for a high-level overview.

# ---

# ## 🛠️ Additional Guidelines

# 1. Work only with the code in this file; ignore anything outside it.
# 2. If the file is pure boilerplate or auto-generated data, state that and skip deep commentary.
# 3. Do **not** invent behaviour; if uncertain, say “appears to …”.
# 4. Output **only** the final JSON object—no extra prose, no markdown.

# """
# )



class Comment(BaseModel):
    comment_code_range: str  # e.g. "10-16" or "100-250"
    comment_linenumber: str              # line number above which the comment will be placed. e.g "12"
    comment: str            # full comment block, each line starts with //
    comment_kind: str   # e.g. "class", "method", "loop", etc. Optional but recommended

class ChunkAnalysis(BaseModel):
    comments: List[Comment]
    structural_analysis: str  # textual explanation of visible structure
    file_summary: str  # brief summary of the file's purpose and key responsibilities

def get_system_prompt():

    yaml_path = os.path.join("codecomprehender", "analyzer", "prompts", "java_chunk_analysis_comments.yaml") 
    if os.path.exists(yaml_path):
        try:
            with open(yaml_path, 'r', encoding='utf-8') as yf:
                yaml_data = yaml.safe_load(yf)
                return yaml_data.get('system_prompt', '')
        except Exception as e:
            print(f"Failed to load YAML file {yaml_path}: {e}")
            return ""
    return ""

def get_json_files_to_process(root_dir):
    """
    Recursively collect all .json files in the directory that have a corresponding .java file.
    Returns a list of absolute paths to the .json files.
    """
    json_files = []
    for dirpath, _, filenames in os.walk(root_dir):
        java_files = {os.path.splitext(f)[0] for f in filenames if f.endswith('.java')}
        for f in filenames:
            if f.endswith('.json'):
                base_name = os.path.splitext(f)[0]
                if base_name in java_files:
                    json_files.append(os.path.join(dirpath, f))
    return json_files


def process_files_with_progress(root_dir):
    llm_client = LLMClient()
    json_files = get_json_files_to_process(root_dir)
    total_files = len(json_files)
    for idx, json_path in enumerate(json_files, 1):
        try:
            with open(json_path, 'r', encoding='utf-8') as f:
                data = json.load(f)
        except Exception as e:
            print(f"Failed to load {json_path}: {e}")
            continue
        chunks = data.get('chunks', [])
        for chunk in chunks:
            code = chunk.get('content', '')
            system_prmpt = get_system_prompt()
            response = llm_client.chat(system_prmpt, code, structured_format=ChunkAnalysis)
            try:
                llm_result = json.loads(response)
                chunk['comments'] = llm_result.get('comments', [])
                chunk['structural_analysis'] = llm_result.get('structural_analysis', '')
                chunk['file_summary'] = llm_result.get('file_summary', '')
            except Exception as e:
                print(f"LLM response could not be parsed for chunk: {e}\nResponse: {response}")
                chunk['comments'] = []
                chunk['structural_analysis'] = ''
                chunk['file_summary'] = ''
            break  # Only process the first chunk per file
        with open(json_path, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2)
        print(f"Processed file {idx} out of {total_files}: {json_path}")


# if __name__ == "__main__":

#     root_path = ".\examples\kitchensink-main"  # Change this as needed
#     process_files_with_progress(root_path)
