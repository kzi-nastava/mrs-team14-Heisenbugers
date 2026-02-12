import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Message } from '../components/chat/chat.component';

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private socket!: WebSocket;
  private messagesSubject = new Subject<Message>();

  connect(): void {
    this.socket = new WebSocket('ws://localhost:8080');

    this.socket.onmessage = (event) => {
    const msg: Message = JSON.parse(event.data); // parse incoming JSON
    this.messagesSubject.next(msg);
  };
  }

  sendMessage(message: Message): void {
    this.socket.send(JSON.stringify(message)); // send as JSON string
  }

  getMessages(): Observable<Message> {
    return this.messagesSubject.asObservable();
  }
}
