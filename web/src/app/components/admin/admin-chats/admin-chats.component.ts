import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { NgIcon, provideIcons } from "@ng-icons/core";
import { Chat } from "../../../models/chat.model";
import { bootstrapChevronRight, bootstrapPerson } from "@ng-icons/bootstrap-icons";
import { NgClass } from "@angular/common";

@Component({
    selector: 'app-admin-chats',
    templateUrl: './admin-chats.component.html',
    imports: [NgIcon, FormsModule, NgClass],
    viewProviders: [provideIcons({bootstrapPerson, bootstrapChevronRight})]
})
export class AdminChats {

    list: Chat[] = [
    {
        chatId: '1',
        driver: {
            firstName: 'John',
            lastName: 'Doe'
        }
    },
    {
        chatId: '2',
        driver: {
            firstName: 'Jane',
            lastName: 'Smith'
        }
    }
    ];
    filteredList: Chat[] = [...this.list];
    searchTerm: any;
    selectedChat: Chat | null = this.filteredList[0];

    applyFilter() {
    const term = this.searchTerm.toLowerCase();

    this.filteredList = this.list.filter(u =>
        `${u.driver.firstName} ${u.driver.lastName}`
        .toLowerCase()
        .includes(term)
    );
    }

    chooseChat(i: number) {
        this.selectedChat = this.filteredList[i];
    }

}