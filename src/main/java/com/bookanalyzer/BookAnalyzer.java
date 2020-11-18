package com.bookanalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookAnalyzer {

    private final List<Input> buyingOffers = new ArrayList<>();
    private final List<Input> sellingOffers = new ArrayList<>();

    private int buyingSize;
    private int sellingSize;
    private String lastBuyingState = "";
    private String lastSellingState = "";

    public static void main(String[] args) {

        int targetSize = Integer.parseInt(args[0]);

        BookAnalyzer bookAnalyzer = new BookAnalyzer();

        bookAnalyzer.analyze(targetSize);
    }

    private void analyze(int targetsize) {

        //clear output file
        try {

            URL resourceOut = getClass().getClassLoader().getResource("book_analyzer.out");

            new FileWriter(resourceOut.getFile(), false).close();

        } catch (IOException ex) {
            Logger.getLogger(BookAnalyzer.class.getName()).log(Level.SEVERE, "Could not read/write", ex);
        }

        //read input file

        URL resourceIn = getClass().getClassLoader().getResource("book_analyzer.in");

        File myFile = new File(resourceIn.getFile());

        try (Scanner myReader = new Scanner(myFile)) {

            int counter = 0;

            while (myReader.hasNextLine() && counter < 111) {

                counter++;

                String line = myReader.nextLine();

                String[] words = line.split(" ");

                Input input;

                switch (words[1]) {

                    case "A":

                        input = new Input(Long.parseLong(words[0]), words[1], words[2], words[3], Double.parseDouble(words[4]), Integer.parseInt(words[5]));

                        if (input.getSide().equals("B")) {

                            buyingOffers.add(input);

                            buyingSize += input.getSize();

                            if (buyingSize >= targetsize) {

                                double amount = calculateBuyingAmount(targetsize);

                                if (amount > 0) {

                                    generateOutput(String.valueOf(input.getTimestamp()), "S", String.valueOf(amount));

                                    lastBuyingState = "";
                                }

                            } else {
                                // if input does not change calculation
                                //generateOutput(String.valueOf(input.getTimestamp()), "S", "NULL");
                            }

                        } else { //  "S"

                            sellingOffers.add(input);

                            sellingSize += input.getSize();

                            if (sellingSize >= targetsize) {

                                double amount = calculateSellingAmount(targetsize);

                                if (amount > 0) {

                                    generateOutput(String.valueOf(input.getTimestamp()), "B", String.valueOf(amount));

                                    lastSellingState = "";
                                }

                            } else {
                                // if input does not change calculation
                                //generateOutput(String.valueOf(input.getTimestamp()), "B", "NULL");
                            }
                        }

                        break;

                    case "R":

                        input = new Input(Long.parseLong(words[0]), words[1], words[2], Integer.parseInt(words[3]));

                        final Input finalInput = input;

                        buyingOffers.stream().forEach(k -> {

                            if (finalInput.getOrderId().equals(k.getOrderId())) {

                                k.setSize(k.getSize() - finalInput.getSize());

                                buyingSize -= finalInput.getSize();

                                if (buyingSize < targetsize) {

                                    if (!lastBuyingState.equalsIgnoreCase("NA")) {

                                        generateOutput(String.valueOf(finalInput.getTimestamp()), "S", "NA");
                                    }

                                    lastBuyingState = "NA";

                                } else {

                                    double amount = calculateBuyingAmount(targetsize);

                                    if (amount > 0) {
                                        generateOutput(String.valueOf(finalInput.getTimestamp()), "S", String.valueOf(amount));

                                    }

                                    lastBuyingState = "";
                                }
                            }

                        });

                        sellingOffers.stream().forEach(k -> {

                            if (finalInput.getOrderId().equals(k.getOrderId())) {

                                k.setSize(k.getSize() - finalInput.getSize());

                                sellingSize -= finalInput.getSize();

                                if (sellingSize < targetsize) {

                                    if (!lastSellingState.equalsIgnoreCase("NA")) {

                                        generateOutput(String.valueOf(finalInput.getTimestamp()), "B", "NA");

                                    }

                                    lastSellingState = "NA";

                                } else {

                                    double amount = calculateSellingAmount(targetsize);

                                    if (amount > 0) {

                                        generateOutput(String.valueOf(finalInput.getTimestamp()), "B", String.valueOf(amount));

                                    }

                                    lastSellingState = "";

                                }
                            }
                        });

                        break;
                }
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BookAnalyzer.class.getName()).log(Level.SEVERE, "File not found", ex);
        }
    }

    private double calculateSellingAmount(int targetsize) {

        double amount = 0;

        int tempSize = targetsize;

        List<Long> usedOfferList = new ArrayList<>();

        while (tempSize > 0) {

            Input offer = sellingOffers.stream().filter(j -> j.getSize() > 0 && !usedOfferList.contains(j.getTimestamp())).min(Comparator.comparing(Input::getPrice)).orElse(null);

            usedOfferList.add(offer.getTimestamp());

            if (offer == null) {

                return amount;

            } else if (tempSize > offer.getSize()) {

                amount += offer.getSize() * offer.getPrice();

                tempSize -= offer.getSize();

            } else {

                amount += tempSize * offer.getPrice();

                tempSize = 0;
            }
        }
        return amount;
    }

    private double calculateBuyingAmount(int targetsize) {

        double amount = 0;

        int tempSize = targetsize;

        List<Long> usedOfferList = new ArrayList<>();

        while (tempSize > 0) {

            Input offer = buyingOffers.stream().filter(j -> j.getSize() > 0 && !usedOfferList.contains(j.getTimestamp())).max(Comparator.comparing(Input::getPrice)).orElse(null);

            usedOfferList.add(offer.getTimestamp());

            if (offer == null) {

                return amount;

            } else if (tempSize > offer.getSize()) {

                amount += offer.getSize() * offer.getPrice();

                tempSize -= offer.getSize();

            } else {

                amount += tempSize * offer.getPrice();

                tempSize = 0;
            }
        }
        return amount;
    }

    private void generateOutput(String timestamp, String action, String totalIncomeOrExpense) {

        StringBuilder stringBuilder = new StringBuilder(timestamp);
        stringBuilder.append(" ").append(action).append(" ").append(totalIncomeOrExpense);

        System.out.println(stringBuilder.toString());

        writeOutputFile(stringBuilder.toString());
    }

    public void writeOutputFile(String line) {

        try {

            URL resourceOut = getClass().getClassLoader().getResource("book_analyzer.out");

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(resourceOut.getFile(), true))) {

                bufferedWriter.newLine();
                bufferedWriter.append(line);
            }

        } catch (IOException ex) {
            Logger.getLogger(BookAnalyzer.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
