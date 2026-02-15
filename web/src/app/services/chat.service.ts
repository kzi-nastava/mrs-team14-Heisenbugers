import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import {Client, Stomp} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { Message } from '../components/chat/chat.component';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
    constructor(private http: HttpClient) {
    }
  loadChat(chatId: string) {
    this.http.get<Message[]>(`http://localhost:8081/api/me/chat/full`).subscribe({
        next: (data) => {
            data.forEach(msg => this.messagesSubject.next(msg));
        },
        error: (error) => {
            console.warn('Failed to load chat messages:', error);
        }
    });
  }
  private stompClient!: Client;
  private messagesSubject = new Subject<Message>();
  private chatId: string = '';

  // Expose messages as observable
  getMessages(): Observable<Message> {
    return this.messagesSubject.asObservable();
  }

  // Connect to WebSocket
  connect(isAdmin: boolean = false): void {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8081/ws'),
      connectHeaders: {
        Authorization: `Bearer ${localStorage.getItem('accessToken')}`
      },
      reconnectDelay: 5000,
      debug: (str) => {
        console.log(str);
      }
    });

    this.stompClient.onConnect = () => {
      if (isAdmin) {
        this.stompClient.subscribe('/topic/admin', (msg) => {
          this.messagesSubject.next(JSON.parse(msg.body));
        });
      } else {
        this.stompClient.subscribe('/user/queue/messages', (msg) => {
          this.messagesSubject.next(JSON.parse(msg.body));
        });
      }
    };

    this.stompClient.activate();

  }

  // Send a message
  sendMessage(message: Message, chatId = this.chatId): void {
    this.stompClient.publish({
      destination: '/app/sendMessage',
      body: JSON.stringify({ ...message, chatId })
    });
  }

}
