/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.eap.pli24.weatherapp.pdf;


import com.eap.pli24.weatherapp.db.City;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Phrase;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


    public class PdfFileCreator {
    
        /**
         * Η μέθοδος δέχεται μια λίστα από αντικείμενα City και ένα path 
         * ως ορίσματα και δημιουργεί ένα αρχείο PDF με τα στατιστικά για τις αναζητήσεις
         * @param citiesViews
         * @param path
         * @return 
         */
        public static boolean createPDFFile(List<City> citiesViews, String path) {
            
            Document document = new Document();
            try {
                // Δημιουργία νέας σύνδεσης με το PDF αρχείο που θα δημιουργηθεί
                PdfWriter.getInstance(document, new FileOutputStream(path + ".pdf"));
                document.open();
                // Δημιουργία βασικής γραμματοσειράς με υποστήριξη unicode
                BaseFont bf = BaseFont.createFont("Aver-opKo.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font font = new Font(bf, 16);
                
                // Δημιουργία τίτλου
                Chunk chunk1 = new Chunk("Στατιστικά Πόλεων", font);
                // Δημιουργία κενής γραμμής
                Paragraph paragraph1 = new Paragraph(chunk1);
                Chunk chunk2 = new Chunk("  ", font);
                Paragraph paragraph2 = new Paragraph(chunk2);
                // Δημιουργία πίνακα PdfPTable με 2 στήλες
                PdfPTable table = new PdfPTable(2);
                // Προσθήκη κεφαλίδας στον πίνακα
                addTableHeader(table, font);
                // Προσθήκη δεδομένων στον πίνακα
                for (City c : citiesViews) {
                    table.addCell(c.getName());
                    table.addCell(String.valueOf(c.getViews()));
                }
                // Προσθήκη τίτλου, κενής γραμμής και πίνακα στο Document
                document.add(paragraph1);
                document.add(paragraph2);
                document.add(table);
            } catch (DocumentException | IOException e) {
                System.out.println("Error at pdf file: " +  e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                // Κλείσιμο του Document στο finally block για να διασφαλιστεί ότι οι πόροι απελευθερώνονται
                if (document != null) {
                    document.close();
                }
            }

            return true;
        }
        /**
         * Η μέθοδος addTableHeader προσθέτει μια κεφαλίδα στον πίνακα με τη βοήθεια της γραμματοσειράς font
         * @param table
         * @param font 
         */
        private static void addTableHeader(PdfPTable table, Font font) {
            PdfPCell header = new PdfPCell();
            // Δημιουργία chunk με το κείμενο "Πόλη"
            Chunk chunk = new Chunk("Πόλη", font);
            //Παραμετροποίηση του τμήματος αυτού του κειμένου
            //// Ορισμός του χρώματος φόντου της κεφαλίδας
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);
            // Ορισμός πάχους περιγράμματος           
            header.setBorderWidth(2);
            // Ορισμός του κειμένου μέσω του chunk
            header.setPhrase(new Phrase(chunk));
            // Προσθήκη της κεφαλίδας στον πίνακα
            table.addCell(header);

            // Επανάληψη των παραπάνω βημάτων για τη δεύτερη στήλη του πίνακα (Στήλη Προβολές)
            header = new PdfPCell();
            chunk = new Chunk("Προβολές", font);
            header.setBackgroundColor(BaseColor.LIGHT_GRAY);            
            header.setBorderWidth(2);
            header.setPhrase(new Phrase(chunk));
            table.addCell(header);
        }
}
    
    

