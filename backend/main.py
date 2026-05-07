"""Local demo API for the AndroidInstrumentation app."""

from datetime import datetime, UTC
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

app = FastAPI(title="AndroidInstrumentation Demo API", version="2.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/health")
def health():
    return {"status": "ok"}


@app.get("/api/v1/unique-post/{post_id}")
def get_unique_post(post_id: int):
    return {
        "id": post_id,
        "userId": 1,
        "title": f"Unique Post #{post_id}",
        "body": (
            "Served by local FastAPI backend. "
            "Called from Android only when you tapped the button."
        ),
    }


@app.get("/api/v1/server-info")
def get_server_info():
    now = datetime.now(UTC).isoformat()
    return {
        "meta": "GET /api/v1/server-info",
        "title": "Server Info",
        "body": f"FastAPI is running. UTC time: {now}",
    }


class EchoNoteRequest(BaseModel):
    note: str


@app.post("/api/v1/echo-note")
def post_echo_note(payload: EchoNoteRequest):
    return {
        "meta": "POST /api/v1/echo-note",
        "title": "Echo Note",
        "body": f"You posted: {payload.note}",
    }


class CreateTicketRequest(BaseModel):
    title: str
    priority: str


@app.post("/api/v1/create-ticket")
def post_create_ticket(payload: CreateTicketRequest):
    ticket_id = f"TCK-{abs(hash(payload.title)) % 100000:05d}"
    return {
        "meta": "POST /api/v1/create-ticket",
        "title": f"Ticket Created: {ticket_id}",
        "body": f"Title: {payload.title} | Priority: {payload.priority}",
    }
