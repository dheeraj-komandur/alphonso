# 🧠 Java Repo Analyzer

## 📚 Table of Contents

- [Objectives](#objectives)
- [Example Results](#example-results)
- [How It Works](#how-it-works)
  - [Step-by-Step Breakdown](#step-by-step-breakdown)
    - [1. Scrape all Java files](#1-scrape-all-java-files)
    - [2. Chunk files into logical units](#2-chunk-files-into-logical-units)
    - [3. Analyze each chunk using gpt-4o-mini (commenting + structure)](#3-analyze-each-chunk-using-gpt-4o-mini-commenting--structure)
    - [4. Extract semantic relationships](#4-extract-semantic-relationships)
    - [5. Merge all insights into a summary JSON](#5-merge-all-insights-into-a-summary-json)
    - [6. Generate README.md using 04-miniModel reasoning](#6-generate-readmemd-using-04-minimodel-reasoning)
    - [7. Generate and embed Mermaid diagrams](#7-generate-and-embed-mermaid-diagrams)
    - [8. Produce _commented.java files with annotations](#8-produce-_commentedjava-files-with-annotations)
- [How to Run the Project](#how-to-run-the-project)

## 🎯 Objectives

This project is a Python library that uses large language models (LLMs) to analyze repositories containing production-grade legacy Java code. The primary goal is to deeply understand all Java files and generate documentation that helps engineers — especially those new to the codebase — onboard faster and reverse engineer systems more effectively. The system is also capable of generating high-quality comments across Java files to improve readability and understanding.

The two key outputs of this project are:
1. A comprehensive `AI_README.md` file enriched with structural and architectural diagrams
2. Annotated Java files (`_commented.java`) containing context-aware comments inserted at meaningful points

*Diagrams are designed to be visually informative. Comments are high-quality, concise, and placed only where they genuinely help — avoiding both under-commenting and noise.*

---

## 📊 Example Results

This analyzer has already been run on two repositories, and their outputs are available in the `examples` folder:

1. **kitchensink-main**
2. **mongo-jdbc-driver-master**

For each example, you will find:
- An AI-generated `AI_README.md` summarizing the codebase with diagrams and insights
- All Java files annotated with `_commented.java` versions containing high-quality, context-aware comments

These examples demonstrate the system's ability to generate comprehensive documentation and improve code readability for complex, production-grade Java projects.

---

## ⚙️ How It Works

The system processes a Java repository in 8 structured stages:

1. **Scrape all Java files**
2. **Chunk files into logical units**
3. **Analyze each chunk using `gpt-4o-mini` (commenting + structure)**
4. **Extract semantic relationships**
5. **Merge all insights into a summary JSON**
6. **Generate `README.md` using `04-miniModel` reasoning**
7. **Generate and embed Mermaid diagrams**
8. **Produce `_commented.java` files with annotations**

---

### Step-by-Step Breakdown

#### 1. Scrape all Java files
Uses Python’s `os` libraries to recursively walk through the target repository and identify all `.java` files.

#### 2. Chunk files into logical units
Each Java file is chunked based on the token size limit defined in `settings.py`.
> **Note:** Due to the high token limits of `gpt-4o-mini` and `o4-mini`, files in most repositories do not require chunking. Each file is treated as a single chunk. However, future-proof logic for chunking is included to support extremely large files. The `settings.py` file is configured with a generous token limit of 50,000 tokens, ensuring even unusually large files are processed as a single chunk.

#### 3. Analyze each chunk and extract semantic relationships

Each chunk is sent to `gpt-4o-mini` with a custom prompt (`prompts/java_chunk_analysis_comments.yaml`). The model is guided to:
- Insert concise, context-aware comments at the class, method, and complex logic block levels, focusing on clarity and developer onboarding.
- Avoid redundant or obvious comments, ensuring only genuinely helpful annotations are added.
- Extract and summarize key entities (classes, interfaces, methods) and their relationships (inheritance, composition, usage).

The model also performs structural analysis to capture relationships such as:
- Class-to-class interactions
- Interface implementations
- Method invocation links

A Pydantic model ensures a structured multi-field response that includes comments, entity maps, and summaries suitable for documentation and diagrams. All responses are aggregated into a single merged JSON file that serves as the central knowledge store for downstream documentation and visualization. This JSON captures, for each processed Java file, key concepts such as `filePath`, `package`, `fileContentSummary`, `structuralAnalysis`, `connections`, `meaning`, and `extra`.

This file is central to downstream documentation and visualization. It is also designed as a future-ready RAG (Retrieval-Augmented Generation) knowledge store for contextual retrieval (currently marked as TODO).

#### 6. Generate `AI_README.md` using `o4-miniModel` reasoning
The merged JSON is passed to `o4-miniModel`, which:
- Performs a global reasoning sweep across all files
- Identifies the high-level purpose and system behavior
- Lays out the full structure for the `README.md`, including:
  - Overview
  - Architecture
  - Modules
  - File responsibilities
  - Execution flow

The model fills in each section with insightful content and marks areas where diagrams would enhance comprehension (e.g., `[DIAGRAM: class_map]`).

> *Note: This model does not generate diagrams directly — it only tags their required location and type.*

#### 7. Generate and embed Mermaid diagrams
For each diagram placeholder:
- All relevant files are collected
- Structural summaries are passed to a new `gpt-4o-mini` call
- A Mermaid diagram is generated using structural reasoning (not raw code)

This Mermaid code is sent to a Mermaid rendering API, returning the diagram image (`.png`), which is then embedded in the final `AI_README.md` at the appropriate section.

#### 8. Produce `_commented.java` files with annotations
The final step creates a new file for every `.java` file:
- Output format: `YourClass_commented.java`
- Original files are untouched
- AI-generated comments are inserted in-line but spaced for clarity
- Comments focus on class-level, method-level, and key logic blocks

These annotated files serve as an auxiliary reference to enhance code readability and developer onboarding.

---

## 🚀 How to Run the Project

1. **Download the repository you want to analyze** and extract it into the `examples` folder of this project.

2. **Run the analyzer** using the following command:

    ```bash
    python ./codecomprehender/analyzer/main.py ./examples/<your-repo-folder>/
    ```

3. **Outputs generated:**
    - `AI_README.md`: An AI-generated summary with diagrams and insights.
    - Annotated Java files: Each `.java` file will have a corresponding `_commented.java` version with high-quality, context-aware comments.

*Note: The original Java files remain unchanged. All outputs are placed alongside the originals in the specified example folder.*
