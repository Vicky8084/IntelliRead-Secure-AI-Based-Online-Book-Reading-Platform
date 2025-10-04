package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.BookConverter;
import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.repository.BookRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.BookRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    BookRepository bookRepository;
    @Autowired
    public BookService(BookRepository bookRepository){
        this.bookRepository=bookRepository;
    }

    public String addBook(BookRequestDTO bookRequestDTO){
        Book book= BookConverter.convertBookRequestDtoIntoBook(bookRequestDTO);
        bookRepository.save(book);
        return "Book saved Successfully";
    }

    public Book findBookById(int id){
        Optional<Book> bookOptional=bookRepository.findById(id);
        if(bookOptional.isPresent()){
            return bookOptional.get();
        }
        else {
            return null;
        }
    }

    public List<Book> findAllBook(){
        return bookRepository.findAll();
    }

    public String updateBook(int id, BookRequestDTO bookRequestDTO){
        Book book=findBookById(id);
        book.setTitle(bookRequestDTO.getTitle());
        book.setAuthor(bookRequestDTO.getAuthor());
        book.setDescription(bookRequestDTO.getDescription());
        book.setLanguage(bookRequestDTO.getLanguage());
        bookRepository.save(book);
        return "Book Updated Successfully";
    }

    public String deleteBook(int id){
        Book book= findBookById(id);
        if(book!=null){
            bookRepository.deleteById(id);
            return "Book Deleted Successfully";
        }
        else{
            return "Book Not Found ";
        }
    }

}
