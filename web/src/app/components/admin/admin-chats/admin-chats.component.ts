import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { Chat } from "../../../models/chat.model";
import { bootstrapChevronRight, bootstrapPerson } from "@ng-icons/bootstrap-icons";
import { NgClass } from "@angular/common";
import { ChatComponent } from "../../chat/chat.component";
import { HttpClient } from "@angular/common/http";

@Component({
    selector: 'app-admin-chats',
    templateUrl: './admin-chats.component.html',
    imports: [NgIcon, FormsModule, NgClass, ChatComponent],
    viewProviders: [provideIcons({bootstrapPerson, bootstrapChevronRight})]
})
export class AdminChats {

    list: Chat[] = [];
    filteredList: Chat[] = [...this.list];
    searchTerm: string = '';
    selectedChat?: Chat;

    constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {
        
    }

    ngOnInit() {
        this.loadChats();
    }

    loadChats() {
        this.http.get<Chat[]>('http://localhost:8081/api/admin/chats').subscribe({
            next: (data) => {
                this.list = data;
                this.applyFilter();
                this.cdr.markForCheck();
            },
            error: (error) => {
                console.warn('Failed to load chats:', error);
            }
        });
    }


    applyFilter() {
    const term = this.searchTerm.toLowerCase();

    this.filteredList = this.list.filter(u =>
        `${u.driver.firstName} ${u.driver.lastName}`
        .toLowerCase()
        .includes(term)
    );
    }

    chooseChat(i: number | null) {
        if (i === null) {
            this.selectedChat = undefined;
        } else {
            this.selectedChat = this.filteredList[i];
        }
        
    }

}