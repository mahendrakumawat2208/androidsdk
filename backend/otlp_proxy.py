import socket
import threading


LISTEN_HOST = "127.0.0.1"
LISTEN_PORT = 32002
TARGET_HOST = "10.167.166.101"
TARGET_PORT = 32002


def pump(src: socket.socket, dst: socket.socket) -> None:
    try:
        while True:
            data = src.recv(65536)
            if not data:
                break
            dst.sendall(data)
    except OSError:
        pass
    finally:
        try:
            src.close()
        except OSError:
            pass
        try:
            dst.close()
        except OSError:
            pass


def main() -> None:
    listener = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    listener.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    listener.bind((LISTEN_HOST, LISTEN_PORT))
    listener.listen(64)
    print(
        f"OTLP proxy listening on {LISTEN_HOST}:{LISTEN_PORT} -> "
        f"{TARGET_HOST}:{TARGET_PORT}",
        flush=True,
    )

    while True:
        client, _ = listener.accept()
        try:
            server = socket.create_connection((TARGET_HOST, TARGET_PORT), timeout=8)
        except OSError:
            client.close()
            continue

        threading.Thread(target=pump, args=(client, server), daemon=True).start()
        threading.Thread(target=pump, args=(server, client), daemon=True).start()


if __name__ == "__main__":
    main()
