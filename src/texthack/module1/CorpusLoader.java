package texthack.module1;

import texthack.collections.SimpleList;
import texthack.collections.SimpleMap;

/**
 * Loads and indexes a sample Indian-language Wikipedia corpus.
 * Each "document" is a title + body of text. The loader builds a simple
 * inverted index for fast keyword lookups. No java.util.* used.
 */
public class CorpusLoader {

    /** A single document in the corpus. */
    public static class Document {
        public final int id;
        public final String title;
        public final String body;
        public final String[] citations;  // titles this doc cites

        public Document(int id, String title, String body, String[] citations) {
            this.id = id;
            this.title = title;
            this.body = body;
            this.citations = citations;
        }

        public String fullText() {
            return title + " " + body;
        }

        @Override
        public String toString() {
            return "[" + id + "] " + title;
        }
    }

    private final SimpleList<Document> documents;
    private final SimpleMap<String, SimpleList<Integer>> invertedIndex;

    public CorpusLoader() {
        documents = new SimpleList<>();
        invertedIndex = new SimpleMap<>();
        loadSampleCorpus();
        buildInvertedIndex();
    }

    /** Returns all documents. */
    public SimpleList<Document> getDocuments() {
        return documents;
    }

    /** Returns document by id, or null. */
    public Document getDocument(int id) {
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).id == id) return documents.get(i);
        }
        return null;
    }

    /** Returns document ids containing the given word. */
    public SimpleList<Integer> search(String word) {
        String key = word.toLowerCase();
        SimpleList<Integer> result = invertedIndex.get(key);
        return result != null ? result : new SimpleList<>();
    }

    /** Returns all document bodies concatenated for string-algorithm demos. */
    public String getFullCorpusText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < documents.size(); i++) {
            if (i > 0) sb.append("\n\n");
            sb.append(documents.get(i).fullText());
        }
        return sb.toString();
    }

    /** Builds the citation adjacency — returns docId → list of cited docIds. */
    public SimpleMap<Integer, SimpleList<Integer>> getCitationGraph() {
        SimpleMap<String, Integer> titleToId = new SimpleMap<>();
        for (int i = 0; i < documents.size(); i++) {
            titleToId.put(documents.get(i).title.toLowerCase(), documents.get(i).id);
        }
        SimpleMap<Integer, SimpleList<Integer>> graph = new SimpleMap<>();
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            SimpleList<Integer> neighbours = new SimpleList<>();
            for (String cite : doc.citations) {
                Integer target = titleToId.get(cite.toLowerCase());
                if (target != null) neighbours.add(target);
            }
            graph.put(doc.id, neighbours);
        }
        return graph;
    }

    // ---------------------------------------------------------------
    //  Sample corpus: snippets inspired by Indian-language Wikipedia
    // ---------------------------------------------------------------
    private void loadSampleCorpus() {
        documents.add(new Document(0, "Taj Mahal",
            "The Taj Mahal is an ivory-white marble mausoleum on the right bank of the river Yamuna "
          + "in Agra, Uttar Pradesh, India. It was commissioned in 1632 by the Mughal emperor Shah Jahan "
          + "to house the tomb of his favourite wife, Mumtaz Mahal. The tomb is the centrepiece of a "
          + "17-hectare complex which includes a mosque and a guest house. Construction of the mausoleum "
          + "was essentially completed in 1643, but work continued on other phases for another 10 years. "
          + "The Taj Mahal complex is believed to have been completed in its entirety in 1653 at a cost "
          + "estimated at the time at around 32 million rupees. The construction project employed some "
          + "20,000 artisans under the guidance of a board of architects led by Ustad Ahmad Lahauri.",
            new String[]{"Mughal Architecture", "Agra"}));

        documents.add(new Document(1, "Mughal Architecture",
            "Mughal architecture is a type of Indo-Islamic architecture developed by the Mughals in the "
          + "16th, 17th and 18th centuries throughout the ever-changing extent of their empire. It is a "
          + "remarkably symmetrical and decorative amalgam of Persian, Turkish, and Indian architecture. "
          + "Mughal buildings have a uniform pattern of structure and character, including large bulbous "
          + "domes, slender minarets at the corners, massive halls, large vaulted gateways and delicate "
          + "ornamentation. Examples include the Red Fort, Humayun's Tomb, and the Badshahi Mosque.",
            new String[]{"Taj Mahal", "Red Fort"}));

        documents.add(new Document(2, "Agra",
            "Agra is a city on the banks of the Yamuna river in the Indian state of Uttar Pradesh. It "
          + "is located about 206 kilometres south of the national capital New Delhi. Agra is one of the "
          + "most populous cities in Uttar Pradesh and the 24th most populous city in India. Agra's "
          + "strategic location on the banks of the Yamuna made it a centre of Mughal power. The city "
          + "flourished under the Mughal emperors and was the capital of the Mughal Empire under Akbar, "
          + "Jahangir, and Shah Jahan. Agra is a major tourist destination because of its many splendid "
          + "Mughal-era buildings, most notably the Taj Mahal, Agra Fort, and Fatehpur Sikri.",
            new String[]{"Taj Mahal", "Mughal Architecture"}));

        documents.add(new Document(3, "Red Fort",
            "The Red Fort is a historic fort in the city of Old Delhi in India that served as the main "
          + "residence of the Mughal emperors. Emperor Shah Jahan commissioned construction of the Red "
          + "Fort on 12 May 1638, when he decided to shift his capital from Agra to Delhi. The fort was "
          + "designed by architect Ustad Ahmad Lahauri, who also constructed the Taj Mahal. The Red Fort "
          + "is named for its massive enclosing walls of red sandstone. Construction began in May 1639 "
          + "and was completed in April 1648. The fort complex consists of many structures including the "
          + "Diwan-i-Am, Diwan-i-Khas, Rang Mahal, and the Moti Masjid. Every year on Independence Day, "
          + "the Prime Minister hoists the Indian national flag at the main gate of the Red Fort.",
            new String[]{"Mughal Architecture", "Agra", "Taj Mahal"}));

        documents.add(new Document(4, "Indian Mathematics",
            "Indian mathematics emerged in the Indian subcontinent from 1200 BCE until the end of the "
          + "18th century. In the classical period of Indian mathematics (400 CE to 1200 CE), important "
          + "contributions were made by scholars like Aryabhata, Brahmagupta, Bhaskara II, and the Kerala "
          + "school of astronomy and mathematics. Aryabhata gave the value of pi as 3.1416 and computed "
          + "sine tables. Brahmagupta introduced rules for computing with zero and negative numbers. The "
          + "Kerala school developed infinite series expansions for trigonometric functions anticipating "
          + "the development of calculus by over two centuries. The decimal number system and the concept "
          + "of zero are perhaps the most significant contributions of Indian mathematics to the world.",
            new String[]{"Aryabhata", "Kerala School"}));

        documents.add(new Document(5, "Aryabhata",
            "Aryabhata (476–550 CE) was an Indian mathematician and astronomer. He is the author of "
          + "several treatises on mathematics and astronomy. His major work, Aryabhatiya, written in 499 "
          + "CE, covers arithmetic, algebra, plane trigonometry, and spherical trigonometry. He also "
          + "computed the circumference of the Earth as 39,968 kilometres, which is close to the current "
          + "equatorial value of 40,075 kilometres. Aryabhata correctly insisted that the Earth rotates "
          + "about its axis daily and that the apparent movement of the stars is a relative motion caused "
          + "by the rotation. His work was influential in the Islamic Golden Age translation movement.",
            new String[]{"Indian Mathematics", "Kerala School"}));

        documents.add(new Document(6, "Kerala School",
            "The Kerala school of astronomy and mathematics was a school of mathematics and astronomy "
          + "founded by Madhava of Sangamagrama in Kerala, India. The school flourished between the 14th "
          + "and 16th centuries. In attempting to solve astronomical problems, the Kerala school "
          + "independently created a number of important mathematics concepts including infinite series "
          + "for trigonometric functions and early forms of differentiation and integration. Nilakantha "
          + "Somayaji developed a model of the solar system placing all planets in orbit around the Sun. "
          + "The works of the Kerala school were first written in Sanskrit and later in Malayalam.",
            new String[]{"Indian Mathematics", "Aryabhata"}));

        documents.add(new Document(7, "Indian Railways",
            "Indian Railways is the fourth-largest railway network in the world by size. It is owned and "
          + "operated by the Government of India through the Ministry of Railways. It runs approximately "
          + "13,000 passenger trains daily on both long-distance and suburban routes, from 7,349 stations "
          + "across India. The network transports over 8 billion passengers annually. The first railway "
          + "on Indian soil was a 34-kilometre line from Bombay to Thane opened on 16 April 1853. Indian "
          + "Railways has been a key driver of India's economic growth and a significant employer with "
          + "over 1.2 million employees. It operates trains across diverse terrains from the Thar Desert "
          + "to the Himalayan foothills.",
            new String[]{"Indian Economy"}));

        documents.add(new Document(8, "Indian Economy",
            "The economy of India is a middle-income developing market economy. It is the world's "
          + "fifth-largest economy by nominal GDP and the third-largest by purchasing power parity. India "
          + "has the world's sixth-largest consumer market and is the world's largest producer of milk, "
          + "pulses, and spices. The country's economic reforms in 1991 included industrial deregulation, "
          + "privatisation of state-owned enterprises, and reduced controls on foreign trade and "
          + "investment. These reforms have transformed India from a largely agrarian economy into a "
          + "diversified economy with significant contributions from the information technology, "
          + "pharmaceutical, and automobile sectors. Major economic hubs include Mumbai, Delhi, Bangalore, "
          + "and Hyderabad.",
            new String[]{"Indian Railways"}));

        documents.add(new Document(9, "Classical Indian Music",
            "Classical Indian music is a genre of South Asian music. It has two major traditions: the "
          + "North Indian classical music known as Hindustani and the South Indian expression known as "
          + "Carnatic. The concept of raga, which describes melodic modes, and tala, which describes "
          + "rhythmic patterns, are fundamental to both systems. Hindustani music was influenced by "
          + "ancient Hindu musical traditions and Persian performance practices. Major instruments include "
          + "the sitar, tabla, sarangi, and harmonium. Carnatic music emphasises vocal music with "
          + "instruments like the veena, mridangam, and violin serving as accompaniment. The tradition "
          + "traces its roots to the Samaveda, one of the four Vedas of Hinduism.",
            new String[]{}));
    }

    private void buildInvertedIndex() {
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            String text = doc.fullText().toLowerCase();
            String[] words = splitWords(text);
            for (String word : words) {
                if (word.isEmpty()) continue;
                SimpleList<Integer> posting = invertedIndex.get(word);
                if (posting == null) {
                    posting = new SimpleList<>();
                    invertedIndex.put(word, posting);
                }
                // avoid duplicate doc ids in the posting list
                if (posting.isEmpty() || posting.get(posting.size() - 1) != doc.id) {
                    posting.add(doc.id);
                }
            }
        }
    }

    /** Simple word splitter — splits on non-alphanumeric characters. */
    private static String[] splitWords(String text) {
        // Hand-built split since we avoid java.util.*
        SimpleList<String> tokens = new SimpleList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                current.append(c);
            } else {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            }
        }
        if (current.length() > 0) tokens.add(current.toString());
        String[] result = new String[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) result[i] = tokens.get(i);
        return result;
    }
}
