import sys
import json
import requests
import faiss
import numpy as np
from pathlib import Path

DATA_DIR = Path("D:/mcp-server/data")
INDEX_FILE = DATA_DIR / "faiss.index"
META_FILE = DATA_DIR / "faiss_meta.json"

DATA_DIR.mkdir(exist_ok=True)

text = sys.argv[1]
memory_id = sys.argv[2]

# 1. Get embedding from Ollama
resp = requests.post(
    "http://localhost:11434/api/embeddings",
    json={
        "model": "nomic-embed-text",
        "prompt": text
    }
).json()

vector = np.array(resp["embedding"], dtype="float32")
dim = vector.shape[0]

# 2. Load or create index
if INDEX_FILE.exists():
    index = faiss.read_index(str(INDEX_FILE))
else:
    index = faiss.IndexFlatL2(dim)

index.add(vector.reshape(1, -1))
faiss.write_index(index, str(INDEX_FILE))

# 3. Save metadata
meta = []
if META_FILE.exists():
    meta = json.loads(META_FILE.read_text())

meta.append({
    "faiss_id": len(meta),
    "memory_id": int(memory_id)
})

META_FILE.write_text(json.dumps(meta))
