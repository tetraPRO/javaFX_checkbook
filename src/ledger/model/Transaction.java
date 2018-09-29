package ledger.model;

import javafx.beans.property.SimpleStringProperty;

public class Transaction {
    public SimpleStringProperty date;
    public SimpleStringProperty to;
    public SimpleStringProperty from;
    public SimpleStringProperty amount;
    public SimpleStringProperty notes;

    Transaction(String dateOf, String toOf, String fromOf, String amountOf, String notesOf){
        this.date = new SimpleStringProperty(dateOf);
        this.to = new SimpleStringProperty(toOf);
        this.from = new SimpleStringProperty(fromOf);
        this.amount = new SimpleStringProperty(amountOf);
        this.notes = new SimpleStringProperty(notesOf);
    }


    public String getFrom() {
        return from.get();
    }
    public void setFrom(String from) {
        this.from.set(from);
    }

    public String getDate() { return date.get(); }
    public void setDate(String date) { this.date.set(date); }

    public String getTo() {
        return to.get();
    }
    public void setTo(String to) {
        this.to.set(to);
    }

    public String getAmount() {
        return amount.get();
    }
    public void setAmount(String amount) {
        this.amount.set(amount);
    }

    public String getNotes() {
        return notes.get();
    }
    public void setNotes(String notes) {
        this.notes.set(notes);
    }


}
