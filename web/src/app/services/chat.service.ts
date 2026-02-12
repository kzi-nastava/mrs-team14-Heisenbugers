import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { Message } from '../components/chat/chat.component';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private stompClient!: Stomp.Client;
  private messagesSubject = new Subject<Message>();
  private chatId: string = '';

  // Expose messages as observable
  getMessages(): Observable<Message> {
    return this.messagesSubject.asObservable();
  }

  // Connect to WebSocket
  connect(isAdmin: boolean = false): void {
    const socket = new SockJS('http://localhost:8081/ws');
    this.stompClient = Stomp.over(socket);

    this.stompClient.connect({Authorization: `Bearer ${localStorage.getItem('accessToken')}`}, () => {
      if (isAdmin) {
        // Admin receives all messages
        this.stompClient.subscribe('/topic/admin', (msg) => {
          this.messagesSubject.next(JSON.parse(msg.body));
        });
      } else {
        // User receives only their messages
        this.stompClient.subscribe(`/user/queue/messages`, (msg) => {
          this.messagesSubject.next(JSON.parse(msg.body));
        });
      }
    });
  }

  // Send a message
  sendMessage(message: Message, chatId = this.chatId): void {
    this.stompClient.send('/app/sendMessage', {}, JSON.stringify({...message, chatId: chatId}));
  }
}
