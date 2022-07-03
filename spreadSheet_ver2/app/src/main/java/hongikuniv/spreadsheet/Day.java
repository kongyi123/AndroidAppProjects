package hongikuniv.spreadsheet;

public class Day {
    String date_string;
    String three_keyword;
    String subject;
    String reading;
    String feedback;
    String daily_record;

    Day(String date_string, String three_keyword, String subject, String reading, String feedback, String daily_record) {
        this.date_string = date_string;
        this.three_keyword = three_keyword;
        this.daily_record = daily_record;
        this.subject = subject;
        this.reading = reading;
        this.feedback = feedback;
    }
}