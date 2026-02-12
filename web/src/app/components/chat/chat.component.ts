import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { ChatService } from '../../services/chat.service';
import { NgClass, NgStyle } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';

export interface Message {
    content: string;
    from: string;
    sentAt: Date;
}

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  imports: [NgClass, FormsModule, NgStyle, DatePipe],
  standalone: true,
})
export class ChatComponent implements OnInit {

  messages: Message[] = [];
  @Input() chatId: string = "";
  
  emptyMessage: Message = { content: '', from: 'me', sentAt: new Date() };
  newMessage: Message = {...this.emptyMessage};
  borderRadius: number = 100; // start fully rounded
  inputRows: number = 1;

  constructor(private chatService: ChatService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.chatService.connect();

    this.chatService.getMessages().subscribe(message => {
      this.messages.push(message);
      this.cdr.markForCheck();
    });
  }

  send(): void {
    if (!this.newMessage.content.trim()) return;

    const message: Message = { ...this.newMessage, sentAt: new Date() }

    this.chatService.sendMessage(message, this.chatId);
    // this.messages.push(message);
    this.newMessage = {...this.emptyMessage};

    this.borderRadius = 100;
+   this.cdr.markForCheck();
  }

  autoResize(event: Event) {
  const textarea = event.target as HTMLTextAreaElement;

  // Resize height
  textarea.style.height = 'auto';
  textarea.style.height = textarea.scrollHeight + 'px';

  // Adjust border radius (less rounded as it grows)
  const maxRadius = 100; 
  const minRadius = 8;  
  const maxHeight = 120;

  this.borderRadius = Math.max(
    minRadius,
    maxRadius - (textarea.scrollHeight / maxHeight) * (maxRadius - minRadius)
  );
}
}
