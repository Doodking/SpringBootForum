package main.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotBlank(message = "Field must not be empty!")
    @Length(max = 2048, message = "It's the biggest post i've ever seen")
    private String text;

    private String tag;

    private String filename;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    public String getAuthorName(){
        if (author != null){
            return author.getUsername();
        }
        else {
            return "none";
        }
    }

    public Post() {}

    public Post(String text, String tag, User user) {
        this.author = user;
        this.text = text;
        this.tag = tag;
    }
}
