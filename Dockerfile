FROM ollama/ollama:0.3.2 as ollama

FROM ghcr.io/chroma-core/chroma:0.5.5 as chroma

FROM testcontainers/ryuk:0.7.0 as ryuk