import os
import json
import tiktoken
import settings

def chunk_text_by_tokens(text, chunk_token_limit, encoding_name="cl100k_base"):
    """
    Splits the text into chunks, each with at most chunk_token_limit tokens, ending on a newline.
    Returns a list of string chunks.
    """
    enc = tiktoken.get_encoding(encoding_name)
    lines = text.splitlines(keepends=True)
    chunks = []
    current_chunk = ""
    current_tokens = 0
    for line in lines:
        line_tokens = len(enc.encode(line))
        if current_tokens + line_tokens > chunk_token_limit and current_chunk:
            chunks.append(current_chunk)
            current_chunk = ""
            current_tokens = 0
        current_chunk += line
        current_tokens += line_tokens
    if current_chunk:
        chunks.append(current_chunk)
    return chunks

def chunk_text_by_tokens_with_metadata(text, chunk_token_limit, java_file_path, root_path, encoding_name="cl100k_base"):
    """
    Splits the text into chunks, each with at most chunk_token_limit tokens, ending on a newline.
    Each chunk is a dict with metadata: file path, abs path, chunk id, content.
    Each line in the chunk content is wrapped with <line_number>line number-</line_number>.
    For every chunk starting from the second, prepend an overlap from the previous chunk (25% of chunk_token_limit or at least 5 lines).
    """
    enc = tiktoken.get_encoding(encoding_name)
    lines = text.splitlines(keepends=True)
    chunks = []
    current_chunk = ""
    current_tokens = 0
    chunk_idx = 1
    current_line = 1
    base_filename = os.path.splitext(os.path.basename(java_file_path))[0]
    chunk_line_ranges = []  # To track line numbers for each chunk
    chunk_contents = []     # To store raw chunk content for overlap
    for idx, line in enumerate(lines, 1):
        # Wrap each line with the line_number HTML tag
        wrapped_line = f'<line_number-{idx}>{line}'
        line_tokens = len(enc.encode(line))
        if current_tokens + line_tokens > chunk_token_limit and current_chunk:
            chunk_contents.append(current_chunk)
            chunk_line_ranges.append((chunk_idx, current_line - len(current_chunk.splitlines()) + 1, idx - 1))
            current_chunk = ""
            current_tokens = 0
            chunk_idx += 1
        current_chunk += wrapped_line
        current_tokens += line_tokens
        current_line = idx
    if current_chunk:
        chunk_contents.append(current_chunk)
        chunk_line_ranges.append((chunk_idx, current_line - len(current_chunk.splitlines()) + 1, current_line))
    # Now add overlap to each chunk (from the second chunk onwards)
    overlap_token_count = max(int(chunk_token_limit * 0.25), 1)
    overlap_line_count = 5
    overlapped_chunks = []
    for i, content in enumerate(chunk_contents):
        if i == 0:
            overlapped_content = content
        else:
            # Get overlap from previous chunk
            prev_content = chunk_contents[i-1]
            prev_lines = prev_content.splitlines(keepends=True)
            # Try to get at least overlap_token_count tokens or overlap_line_count lines
            overlap_lines = []
            token_sum = 0
            for line in reversed(prev_lines):
                token_sum += len(enc.encode(line))
                overlap_lines.insert(0, line)
                if token_sum >= overlap_token_count or len(overlap_lines) >= overlap_line_count:
                    break
            overlapped_content = ''.join(overlap_lines) + content
        chunk_id = f"{base_filename}_{i+1}"
        chunk_data = {
            "file_path": os.path.relpath(java_file_path, root_path),
            "abs_file_path": os.path.abspath(java_file_path),
            "chunk_id": chunk_id,
            "content": overlapped_content
        }
        overlapped_chunks.append(chunk_data)
    return overlapped_chunks

def chunk_java_file(java_file_path, root_path):
    import tiktoken
    with open(java_file_path, "r", encoding="utf-8") as f:
        java_code = f.read()
    chunk_token_limit = settings.CHUNK_TOKEN_LIMIT
    enc = tiktoken.get_encoding("cl100k_base")
    total_tokens = len(enc.encode(java_code))
    chunks = chunk_text_by_tokens_with_metadata(java_code, chunk_token_limit, java_file_path, root_path)
    exceeded = total_tokens > chunk_token_limit
    base, _ = os.path.splitext(java_file_path)
    json_file_path = base + ".json"
    data = {
        "java_file": java_file_path,
        "chunks": chunks
    }
    with open(json_file_path, "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2)
    return {
        'file': os.path.relpath(java_file_path, root_path),
        'tokens': total_tokens,
        'exceeded': exceeded
    }

def walk_and_chunk_java_files_in_directory(root_path):
    table_data = []
    java_file_count = 0
    for dirpath, _, filenames in os.walk(root_path):
        for filename in filenames:
            if filename.endswith('.java'):
                java_file_path = os.path.join(dirpath, filename)
                result = chunk_java_file(java_file_path, root_path)
                table_data.append(result)
                java_file_count += 1
    # if table_data:
    #     print("{:>12} {:>18} {:<60}".format("# Tokens", "Exceeded Max Token?", "File Name"))
    #     print("-" * 92)
    #     for row in table_data:
    #         print("{:>12} {:>18} {:<60}".format(row['tokens'], "Alert - Yes" if row['exceeded'] else "No", row['file']))
    
    for row in table_data:
        if row['exceeded']:
            print(f"Alert - One File is Above Token Limit: {row['file']} ({row['tokens']} tokens)")
    if not any(row['exceeded'] for row in table_data):
        print("Note - No file exceeded the token limit.")


# def main():
#     # Example: hardcoded path for demonstration
#     root_path = ".\examples\kitchensink-main"  # Change this as needed
#     walk_and_chunk_java_files_in_directory(root_path)

# if __name__ == "__main__":
#     main()
