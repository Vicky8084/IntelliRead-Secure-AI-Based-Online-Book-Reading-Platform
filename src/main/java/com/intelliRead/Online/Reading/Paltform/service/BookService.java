package com.intelliRead.Online.Reading.Paltform.service;

import com.intelliRead.Online.Reading.Paltform.converter.BookConverter;
import com.intelliRead.Online.Reading.Paltform.exception.BookAlreadyExistException;
import com.intelliRead.Online.Reading.Paltform.exception.UserNotFoundException;
import com.intelliRead.Online.Reading.Paltform.model.Book;
import com.intelliRead.Online.Reading.Paltform.model.User;
import com.intelliRead.Online.Reading.Paltform.repository.BookRepository;
import com.intelliRead.Online.Reading.Paltform.repository.UserRepository;
import com.intelliRead.Online.Reading.Paltform.requestDTO.BookRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BookService {

    BookRepository bookRepository;
    UserRepository userRepository;
    @Autowired
    public BookService(BookRepository bookRepository,
                       UserRepository userRepository){
        this.bookRepository=bookRepository;
        this.userRepository=userRepository;

    }

    public String addBook(BookRequestDTO bookRequestDTO){

        Optional<Book> bookOptional= bookRepository.findBookByTitle(bookRequestDTO.getTitle());
        if(bookOptional.isPresent()){
            throw new BookAlreadyExistException("Book"+bookRequestDTO.getTitle()+" already exist!!");
        }

        User user = userRepository.findById(bookRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));


        Book book= BookConverter.convertBookRequestDtoIntoBook(bookRequestDTO);
        //User user=userRepository.findById(bookRequestDTO.getUserId()).get();
        book.setUser(user);
        bookRepository.save(book);
        return "Book saved Successfully";
    }

    public Book findBookById(int id){
        Optional<Book> bookOptional=bookRepository.findById(id);
        return bookOptional.orElse(null);
    }

    public List<Book> findAllBook(){
        return bookRepository.findAll();
    }

    public String updateBook(int id, BookRequestDTO bookRequestDTO){
        Book book=findBookById(id);
        if(book!=null){
            book.setTitle(bookRequestDTO.getTitle());
            book.setAuthor(bookRequestDTO.getAuthor());
            book.setDescription(bookRequestDTO.getDescription());
            book.setLanguage(bookRequestDTO.getLanguage());
            bookRepository.save(book);
            return "Book Updated Successfully";
        }
        else{
            return "book not found";
        }
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
