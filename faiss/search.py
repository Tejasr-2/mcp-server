import sys
import json
import requests
import faiss
import numpy as np
from pathlib import Path

query = sys.argv[1]

DATA_DIR = Path("D:/mcp-server/data")
INDEX_FILE = DATA_DIR / "faiss.index"
META_FILE = DATA_DIR / "faiss_meta.json"

if not INDEX_FILE.exists():
    print("[]")
    sys.exit(0)

# Get embedding
resp = requests.post(
    "http://localhost:11434/api/embeddings",
    json={
        "model": "nomic-embed-text",
        "prompt": query
    }
).json()

vector = np.array(resp["embedding"], dtype="float32")

index = faiss.read_index(str(INDEX_FILE))
D, I = index.search(vector.reshape(1, -1), 5)

meta = json.loads(META_FILE.read_text())
memory_ids = [int(meta[i]["memory_id"]) for i in I[0]]

print(json.dumps(memory_ids))
