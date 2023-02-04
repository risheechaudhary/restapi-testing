package com.neosoft.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.neosoft.restapi.Book;
import com.neosoft.restapi.BookController;
import com.neosoft.restapi.BookRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class BookControllerTest {
    private MockMvc mockMvc;

    ObjectMapper objectMapper=new ObjectMapper();
    ObjectWriter objectWriter=objectMapper.writer();

    @Mock
    BookRepository bookRepository;

    @InjectMocks
    private BookController bookController;

    Book RECORD_1=new Book(1L,"Atomic Habits","How to Build Better Habits",5);
    Book RECORD_2=new Book(2L,"Java","JAVA",5);
    Book RECORD_3=new Book(3L,"Spring Boot","Spring Boot",5);

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc= MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void getAllBookRecords_success() throws Exception{
        List<Book> records = new ArrayList<>(Arrays.asList(RECORD_1,RECORD_2,RECORD_3));

        Mockito.when(bookRepository.findAll()).thenReturn(records);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/book")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].name", is("Spring Boot")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", is("Java")));

    }

    @Test
    public void getBookById_success() throws Exception{
         Mockito.when(bookRepository.findById(RECORD_1.getBookId())).thenReturn(java.util.Optional.of(RECORD_1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/book/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Atomic Habits")));
    }

    @Test
    public void createBookRecord_success() throws Exception{
        Book record = Book.builder()
                .bookId(4L)
                .name("Author name")
                .summary("error expecting")
                .rating(4)
                .build();

        Mockito.when(bookRepository.save(record)).thenReturn(record);

        String content = objectWriter.writeValueAsString(record);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("Author name")));
    }

    @Test
    public void updateBookRecord_success() throws Exception{
        Book updateBookRecord = Book.builder()
                .bookId(1L)
                .name("updated book name")
                .summary("Updated summary")
                .rating(1).build();

        Mockito.when(bookRepository.findById(RECORD_1.getBookId())).thenReturn(java.util.Optional.ofNullable(RECORD_1));
        Mockito.when(bookRepository.save(updateBookRecord)).thenReturn(updateBookRecord);

        String updatedContent = objectWriter.writeValueAsString(updateBookRecord);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(updatedContent);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("updated book name")));

    }

    @Test
    public void deleteBookRecord_success() throws Exception{
        Mockito.when(bookRepository.findById(RECORD_2.getBookId())).thenReturn(Optional.of(RECORD_2));

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/book/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}