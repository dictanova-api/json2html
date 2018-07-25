package com.dictanova;

import com.dictanova.dto.EnrichmentDTO;
import com.dictanova.dto.ItemDTO;
import com.dictanova.dto.PageDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

public class Json2Html {


    public static void main(String[] args) throws Exception {

        File input = new File(args[0]);
        File output = new File(args[1]);

        FileReader inputReader = new FileReader(input);
        FileWriter outputWriter = new FileWriter(output);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        PageDTO pageDTO = gson.fromJson(inputReader, PageDTO.class);

        outputWriter.write("<!DOCTYPE html>\n");
        outputWriter.write("<html lang=\"en\">\n");
        outputWriter.write("<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>json2html</title>\n" +
                "  </head>\n");
        outputWriter.write("<body style=\"font-family : arial; padding:30px; \">\n");
        outputWriter.write("<ul style=\"list-style:none;\">\n");

        for (ItemDTO item : pageDTO.items) {

            outputWriter.write("<li style=\"margin-bottom:20px; border:1px solid gray; padding:10px;\">\n");
            outputWriter.write("<h3>" + item.externalId + "</h3>\n");

            int position = 0;
            EnrichmentDTO currentEnrichment = null;
            char[] chars = item.content.toCharArray();

            for (char ch : chars) {

                if (currentEnrichment != null) {
                    if (position >= currentEnrichment.offset.end) {
                        currentEnrichment = null;
                        outputWriter.write("</span>\n");
                    }
                } else {
                    currentEnrichment = searchPosition(position, item.enrichments);
                    if (currentEnrichment != null) {
                        String cssCOlor = currentEnrichment.opinion.equals("POSITIVE") ?
                                "LightGreen" : (currentEnrichment.opinion.equals("NEGATIVE") ? "LightCoral" : "LightGray");
                        outputWriter.write("<span style=\"background-color:" + cssCOlor + "\">\n");
                    }
                }


                if (ch != '\n') {
                    outputWriter.write(ch);
                } else {
                    outputWriter.write("<br/>");
                }

                position++;
            }


            outputWriter.write("</li>\n");

        }


        outputWriter.write("</ul>\n");
        outputWriter.write("</body>\n");
        outputWriter.write("</html>\n");

        outputWriter.flush();
        outputWriter.close();

        inputReader.close();

    }

    private static EnrichmentDTO searchPosition(int position, List<EnrichmentDTO> enrichments) {

        for (EnrichmentDTO enrichment : enrichments) {

            if (enrichment.offset.begin == position) {
                return enrichment;
            }

        }

        return null;

    }


}
