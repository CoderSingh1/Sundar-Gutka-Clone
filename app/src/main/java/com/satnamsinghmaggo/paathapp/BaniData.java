package com.satnamsinghmaggo.paathapp;

import java.util.ArrayList;
import java.util.List;

public class BaniData {
    public static List<Bani> getBaniList() {
        List<Bani> baniList = new ArrayList<>();
        
        // Add sample banis - replace with your actual data
        baniList.add(new Bani(
            "Japji Sahib",
            "The morning prayer of the Sikhs",
            "Content of Japji Sahib...",
            "https://example.com/audio/japji.mp3"
        ));
        
        baniList.add(new Bani(
            "Jaap Sahib",
            "A prayer composed by Guru Gobind Singh Ji",
            "Content of Jaap Sahib...",
            "https://example.com/audio/jaap.mp3"
        ));
        
        baniList.add(new Bani(
            "Tav Prasad Savaiye",
            "A composition by Guru Gobind Singh Ji",
            "Content of Tav Prasad Savaiye...",
            "https://example.com/audio/tavprasad.mp3"
        ));
        
        // Add more banis as needed
        
        return baniList;
    }
} 