import sys
from create_chunks_java_files import walk_and_chunk_java_files_in_directory
from generate_comments_for_chunks import process_files_with_progress
from merge_comments_analysis_for_repo import merge_structural_analysis
from merge_comments_analysis_for_repo import merge_all_files_analysis
from readme_generation import generate_readme_from_merged_analysis
from readme_generation import process_diagram_tags
from write_comments_to_java_files import add_comments_to_java_file
from temp_clean_json_java import delete_associated_json_and_commented_java

def main():
    if len(sys.argv) < 2:
        print("Usage: python main.py <root_path>")
        sys.exit(1)
    root_path = sys.argv[1]
    delete_associated_json_and_commented_java(root_path)
    print("="*80)
    
    print("Scanning all Java files in the directory and creating chunks...")
    walk_and_chunk_java_files_in_directory(root_path)
    print("Chunks and JSON Files Created Successfully!")

    print("="*80)
    print("Analyis files with LLMs...")
    print("This may take a while, please be patient...")
    process_files_with_progress(root_path)
    print("LLM based analysis Completed Successfully!")

    print("="*80)
    merge_structural_analysis(root_path)
    merge_all_files_analysis(root_path)
    print("Merging analysis files...")
    print("Merging completed Successfully!")

    print("="*80)
    print("Generating README using reasoning model...")
    print("This may take a while, please be patient...")
    generate_readme_from_merged_analysis(root_path)
    print("Making API calls to generate diagrams!")
    process_diagram_tags(root_path)
    print("README generation completed Successfully!")

    print("="*80)
    print("Adding comments to Java files...")
    add_comments_to_java_file(root_path)

    print("Comments added to Java files successfully!")
    print("="*80)

    print("All tasks completed successfully!")
    print("You can now check the Java files for comments and the README file for analysis results.")


if __name__ == "__main__":
    main()
