import os
import shutil


def delete_associated_json_and_commented_java(root_dir):
    """
    Recursively find all .java files in root_dir. For each, delete:
    - The .json file with the same base name in the same directory (if exists)
    - The <JavaFileName>_commented.java file in the same directory (if exists)
    Additionally, in the root_dir, delete:
    - Any folder named 'AI_README_IMAGES'
    - Any file named 'AI_README.md'
    """
    # Delete 'AI_README_IMAGES' folder if it exists in root_dir
    ai_readme_images_dir = os.path.join(root_dir, 'AI_README_IMAGES')
    if os.path.isdir(ai_readme_images_dir):
        try:
            shutil.rmtree(ai_readme_images_dir)
        except Exception:
            pass
    # Delete 'AI_README.md' file if it exists in root_dir
    ai_readme_md = os.path.join(root_dir, 'AI_README.md')
    if os.path.isfile(ai_readme_md):
        try:
            os.remove(ai_readme_md)
        except Exception:
            pass
    
    for dirpath, _, filenames in os.walk(root_dir):
        java_files = [f for f in filenames if f.endswith('.java')]
        for java_file in java_files:
            base_name = os.path.splitext(java_file)[0]
            # Delete associated .json file
            json_file = os.path.join(dirpath, base_name + '.json')
            if os.path.exists(json_file):
                try:
                    os.remove(json_file)
                    print(f"Deleted: {json_file}")
                except Exception as e:
                    print(f"Failed to delete {json_file}: {e}")
            # Delete associated _commented.java file
            commented_java_file = os.path.join(dirpath, base_name + '_commented.java')
            if os.path.exists(commented_java_file):
                try:
                    os.remove(commented_java_file)
                    print(f"Deleted: {commented_java_file}")
                except Exception as e:
                    print(f"Failed to delete {commented_java_file}: {e}")

    


# def main():
#     # Example: hardcoded path for demonstration
#     root_path = ".\examples\kitchensink-main"  # Change this as needed
#     delete_associated_json_and_commented_java(root_path)

# if __name__ == "__main__":
#     main()
