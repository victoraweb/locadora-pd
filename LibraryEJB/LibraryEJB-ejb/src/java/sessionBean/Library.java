/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sessionBean;

import dao.DAOBook;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import model.Book;

/**
 *
 * @author victoraweb
 */
@Stateful
public class Library implements LibraryRemote {

    @PersistenceContext(name = "Library_PU")
    EntityManager em;
    
    @EJB
    CounterLocal searchCounter;
    
    @EJB
    LoggerLocal logger;
    
    @Override
    public boolean addBook(long codigo, String titulo, String editora, long isbn, int edicao, String autor) {
        DAOBook daoBook = new DAOBook(em);
        if (daoBook.findBookByTitle(titulo) == null && daoBook.findBookByIsbn(isbn) == null) {
            Book bookToUpdate = new Book();
            bookToUpdate.setCodigo(codigo);
            bookToUpdate.setTitulo(titulo);
            bookToUpdate.setEditora(editora);
            bookToUpdate.setIsbn(isbn);
            bookToUpdate.setEdicao(edicao);
            bookToUpdate.setAutor(autor);
            
            daoBook.persist(bookToUpdate);
            logger.newLog("Livro cadastrado com sucesso. Código do livro: " + codigo + ". Título do livro: " + titulo);
            return true;
        }
        else {
            logger.newLog("Erro ao cadastrar livro com código: " + codigo + " e  título : " + titulo);
            return false;
        }
    }

    @Override // Update find by code. Use when changing title
    public boolean updateBook(long codigo, String newTitulo, String newEditora, long newIsbn, int newEdicao, String newAutor) {
        DAOBook daoBook = new DAOBook(em);
        Book bookToUpdate = daoBook.findBookByCode(codigo);
        if (bookToUpdate != null) {
            bookToUpdate.setCodigo(codigo);
            bookToUpdate.setTitulo(newTitulo);
            bookToUpdate.setEditora(newEditora);
            bookToUpdate.setIsbn(newIsbn);
            bookToUpdate.setEdicao(newEdicao);
            bookToUpdate.setAutor(newAutor);
            
            daoBook.merge(bookToUpdate);
            logger.newLog("Livro atualizado com sucesso. Código do livro: " + codigo + ".");
            return true;
        }
        else {
            logger.newLog("Erro ao tentar atualizar livro com código: " + codigo + ".");
            return false;
        }
    }
    
    @Override
    public boolean deleteBook(long isbn) {
        DAOBook daoBook = new DAOBook(em);
        Book bookToUpdate = daoBook.findBookByIsbn(isbn);
        
        if (bookToUpdate != null) {
            daoBook.remove(bookToUpdate);
            logger.newLog("Livro removido com sucesso. Isbn do livro removido: " + isbn + ".");
            return true;
        }
        else {
            logger.newLog("Erro ao tentar remover o livro com isbn: " + isbn + ".");
            return false;
        }
    }
    
    @Override
    public boolean deleteBookByTitle(String titulo) {
        DAOBook daoBook = new DAOBook(em);
        Book book = daoBook.findBookByTitle(titulo);
        if (book != null) {
            daoBook.remove(book);
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public String findBookByCode(long codigo) {
        DAOBook daoBook = new DAOBook(em);
        Book book = daoBook.findBookByCode(codigo);
        if (book != null) {
            searchCounter.increaseCounter();
            return book.toString();
        }
        else {
            return "O livro pesquisado com código: " + codigo + " não foi encontrado.";
        }
    }

    @Override
    public String findBookByTitle(String titulo) {
        DAOBook daoBook = new DAOBook(em);
        Book book = daoBook.findBookByTitle(titulo);
        if (book != null) {
            searchCounter.increaseCounter();
            return book.toString();
        }
        else {
            return "O livro pesquisado com o título: " + titulo + " não foi encontrado.";
        }
    }

    @Override
    public String findBookByAuthor(String autor) {
        DAOBook daoBook = new DAOBook(em);
        List<Book> books = daoBook.findBookByAuthor(autor);
        String retrievedBooks = ""; 
        int i = 1;
        if (books != null) {
            for (Book b : books) {
                searchCounter.increaseCounter();
                retrievedBooks += i + " - " + b.toString();
                i++;
            }
            return retrievedBooks;
        }
        else {
            return "Livros com o autor: " + autor + " não foram encontrados.";
        }
    }

    @Override
    public String findBookByIsbn(long isbn) {
        DAOBook daoBook = new DAOBook(em);
        Book book = daoBook.findBookByIsbn(isbn);
        if (book != null) {
            searchCounter.increaseCounter();
            return book.toString();
        }
        return "Nenhum livro com isbn: " + isbn + " foi encontrado";
    }

    @Override
    public String qtdSearch() {
        return "Numero de consultas realizadas no sistema: " + searchCounter.getSequence();
    }

    @Override
    public String findAllBooks() {
        DAOBook daoBook = new DAOBook(em);
        List<Book> books = daoBook.findAllBooks();
        String retrievedBooks = "";
        int i = 1;
        if(books != null) {
            for (Book b : books) {
                searchCounter.increaseCounter();
                retrievedBooks += i + " - " + b.toString();
                i++;
            }
            return retrievedBooks;
        }
        else {
            return "Não há livros cadastrados no sistema ainda =).";
        }
        
    }
    
    @Override
    public String showMenu() {
		String menu = "-------------M--E--N--U-------------\n";
                menu += "0 - Sair;\n";
		menu += "1 - Adicionar livro \n";
		menu += "2 - Atualizar livro; \n"; // Por Código
		menu += "3 - Remover livro;\n"; // Por isbn
                menu += "4 - Listar todos os livros;\n";
		menu += "5 - Pesquisar livro;\n"; // Por título, autor, isbn
		menu += "6 - Verificar quantidade de consultas;\n";
		menu += "7 - MENU;\n";
		menu += "-------------------------------------------";
		return menu;
	}
}
