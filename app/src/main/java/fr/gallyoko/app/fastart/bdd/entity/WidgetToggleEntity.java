package fr.gallyoko.app.fastart.bdd.entity;

public class WidgetToggleEntity {

    private int id;
    private String isbn;
    private String titre;

    public WidgetToggleEntity(){}

    public WidgetToggleEntity(String isbn, String titre){
        this.isbn = isbn;
        this.titre = titre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String toString(){
        return "ID : "+id+"\nISBN : "+isbn+"\nTitre : "+titre;
    }
}