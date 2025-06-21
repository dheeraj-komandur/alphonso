import os
import sys
import json


def get_indentation_for_line(lines, line_num):
    # Find the next lowest line with code (not blank/whitespace)
    for i in range(line_num, len(lines)):
        line = lines[i]
        if line.strip():
            return line[:len(line) - len(line.lstrip())]
    return ''


def clean_comment_text(comment_text):
    comment_text = comment_text.strip()
    if comment_text.startswith('//'):
        comment_text = comment_text[2:].lstrip()
    return comment_text


def insert_comments_into_java(java_file_path, comments):
    # Read the original Java file lines
    with open(java_file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    # Prepare a list of (line_number, comment) tuples, sorted by line_number descending
    # so that insertion does not affect subsequent line numbers
    comment_tuples = []
    for comment in comments:
        try:
            line_num = int(comment.get('comment_linenumber', 0))
        except Exception:
            continue
        comment_text = comment.get('comment', '')
        if comment_text:
            comment_tuples.append((line_num, comment_text))
    comment_tuples.sort(reverse=True, key=lambda x: x[0])
    # Insert comments
    for line_num, comment_text in comment_tuples:
        # Find indentation for the next lowest code line
        indentation = get_indentation_for_line(lines, line_num)
        cleaned_comment = clean_comment_text(comment_text)
        comment_line = f"{indentation}// (AI Comment) - {cleaned_comment}\n"
        # Insert comment as a new line before the specified line number
        # If line_num is 0, insert at the top
        if line_num <= 0:
            lines.insert(0, comment_line)
        elif line_num <= len(lines):
            lines.insert(line_num, comment_line)
        else:
            lines.append(comment_line)
    # Write to new file
    base, ext = os.path.splitext(java_file_path)
    commented_file_path = base + '_commented.java'
    with open(commented_file_path, 'w', encoding='utf-8') as f:
        f.writelines(lines)
    return commented_file_path


def prepend_file_summary_to_commented_java(java_commented_file, json_file):
    # Read the commented Java file
    with open(java_commented_file, 'r', encoding='utf-8') as f:
        java_lines = f.readlines()
    # Read the JSON file and extract file_summary
    with open(json_file, 'r', encoding='utf-8') as jf:
        data = json.load(jf)
    # Assume only one chunk for this file
    file_summary = ''
    for chunk in data.get('chunks', []):
        if 'file_summary' in chunk:
            file_summary = chunk['file_summary']
            break
    if not file_summary:
        return  # Nothing to prepend
    summary_lines = file_summary.strip().split('\n')
    # Prepare the multiline comment block
    filename = os.path.basename(java_commented_file)
    comment_block = [
        f"/*\n",
        f"********* AI-Assistant Documentation for - {filename} *********\n",
        '\n'.join(summary_lines) + '\n',
        "*/\n\n"
    ]
    # Insert at the top
    new_lines = comment_block + java_lines
    # Overwrite the commented Java file
    with open(java_commented_file, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)


def add_comments_to_java_file(root_path):
    for dirpath, dirnames, filenames in os.walk(root_path):
        for filename in filenames:
            if filename.endswith('.java'):
                java_file_path = os.path.join(dirpath, filename)
                json_file = os.path.splitext(java_file_path)[0] + '.json'
                if os.path.exists(json_file):
                    with open(json_file, 'r', encoding='utf-8') as jf:
                        data = json.load(jf)
                    # Collect all comments from all chunks
                    all_comments = []
                    for chunk in data.get('chunks', []):
                        all_comments.extend(chunk.get('comments', []))
                    commented_file_path = insert_comments_into_java(java_file_path, all_comments)
                    prepend_file_summary_to_commented_java(commented_file_path, json_file)
                    # Delete the JSON file after processing
                    os.remove(json_file)
                    

# def main():
#     # Example: hardcoded path for demonstration
#     root_path = r".\examples\kitchensink-main"  # Change this as needed
#     add_comments_to_java_file(root_path)

# if __name__ == "__main__":
#     main()
