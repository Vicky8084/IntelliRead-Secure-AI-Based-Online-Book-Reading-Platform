package com.intelliRead.Online.Reading.Paltform.converter;

import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.requestDTO.BookRequestDTO;

public class BookConverter {
    public static Book convertBookRequestDtoIntoBook(BookRequestDTO bookRequestDTO){
        Book book =new Book();
        book.setTitle(bookRequestDTO.getTitle());
        book.setAuthor(bookRequestDTO.getAuthor());
        book.setDescription(bookRequestDTO.getDescription());
        book.setLanguage(bookRequestDTO.getLanguage());
        return book;
    }
}