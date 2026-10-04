# Chat with your PDFs — 100% local RAG in Java (Spring AI)

Source code for the KloudOcean Academy video **"RAG Tutorial in Java: Build a PDF Chatbot with Spring AI (100% Local)"**.

Upload a PDF, ask questions in plain English, and get answers that cite the exact file and page. Everything runs on your laptop: no API keys, no monthly bill.

- Two Java classes, under 100 lines
- Answers cite their source: `[Brightpath-Employee-Handbook.pdf, page 3]`
- Refuses to guess when the answer isn't in your documents
- Streams answers word by word (Server-Sent Events)
- System prompt hardened against prompt injection hidden inside PDFs

## Stack

Java 21 · Spring Boot 4 · Spring AI 2 · Ollama (`gemma4` + `nomic-embed-text`) · pgvector (PostgreSQL) · Docker Compose

## Run it

1. Install [Ollama](https://ollama.com) and [Docker](https://www.docker.com/), then pull the two models:
   ```bash
   ollama pull gemma4
   ollama pull nomic-embed-text
   ```
   8 GB of RAM? Use a smaller chat model such as `llama3.2` and change `spring.ai.ollama.chat.options.model` in `application.yml`.

2. Start the app. Spring Boot starts the pgvector database from `compose.yaml` for you:
   ```bash
   ./gradlew bootRun
   ```

3. Open http://localhost:8080, upload a PDF from `docs/` and ask a question. Or use curl:
   ```bash
   curl -F file=@docs/Brightpath-Employee-Handbook.pdf localhost:8080/api/documents
   curl -N -G localhost:8080/api/ask --data-urlencode "question=What is the hotel allowance per night?"
   ```

## How it works

| Job | Class | Steps |
|---|---|---|
| Upload a PDF | `DocumentController` | read pages (`PagePdfDocumentReader`) → split into ~300-token chunks (`TokenTextSplitter`) → embed and store (`VectorStore.add`) |
| Ask a question | `ChatController` | retrieve the 5 closest chunks (`similaritySearch`) → put them in the prompt inside `<source>` tags → stream the answer (`ChatClient`) |

## Sample documents

`docs/` contains fictional PDFs made for this demo, including `Falcon-Office-Supplies-Price-List-Q4-2026.pdf`, which hides a prompt-injection attack in its last paragraph. Try asking *"Can Falcon Office Supplies approve my expenses?"* and see rule 4 of the system prompt block it.

## License

MIT
