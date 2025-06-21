import os
import json
from typing import Dict, Any

def find_java_and_json_files(root_dir: str):
    matches = []
    for dirpath, _, filenames in os.walk(root_dir):
        java_files = [f for f in filenames if f.endswith('.java')]
        for java_file in java_files:
            base = os.path.splitext(java_file)[0]
            json_file = base + '.json'
            if json_file in filenames:
                matches.append({
                    'java_path': os.path.join(dirpath, java_file),
                    'json_path': os.path.join(dirpath, json_file)
                })
    return matches

def extract_structural_analysis(json_path: str) -> Dict[str, Any]:
    with open(json_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    result = {}
    if 'chunks' in data and data['chunks']:
        first_chunk = data['chunks'][0]
        result['structural_analysis'] = first_chunk.get('structural_analysis', None)
    # Add the file path from the JSON if present
    if 'java_file' in data:
        file_path = data['java_file']
        if file_path.startswith('.\\examples\\'):
            file_path = file_path[len('.\\examples\\'):]
        result['file_path'] = file_path
    else:
        result['file_path'] = None
    return result if result else None

def merge_structural_analysis(root_dir: str):
    matches = find_java_and_json_files(root_dir)
    merged = {}
    for match in matches:
        java_name = os.path.basename(match['java_path'])
        structural_analysis = extract_structural_analysis(match['json_path'])
        if structural_analysis:
            merged[java_name] = structural_analysis
    output_file = os.path.join(root_dir, "merged_analysis.json")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(merged, f, indent=2)

def merge_all_files_analysis(root_dir: str):
    """
    Merges the full JSON content for all Java files with associated JSON files into rag_index.json.
    The output is a dict keyed by Java filename, with the value being the full JSON content.
    """
    matches = find_java_and_json_files(root_dir)
    merged = {}
    for match in matches:
        java_name = os.path.basename(match['java_path'])
        with open(match['json_path'], 'r', encoding='utf-8') as f:
            try:
                data = json.load(f)
            except Exception as e:
                print(f"Error loading {match['json_path']}: {e}")
                continue
        merged[java_name] = data
    output_file = os.path.join(root_dir, "rag_index.json")
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(merged, f, indent=2)

# if __name__ == '__main__':
#     root_path = ".\examples\kitchensink-main"  # Change this as needed
#     merge_structural_analysis(root_path)
#     merge_all_files_analysis(root_path)


