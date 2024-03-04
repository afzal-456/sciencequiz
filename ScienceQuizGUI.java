import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;


public class ScienceQuizGUI extends JFrame {
    private ArrayList<Question> questions;
    private int currentQuestionIndex;
    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private JButton nextButton;
    private int score;
    private String playerName; // Store the player's name
    private long startTime; // To track start time
    private long endTime;   // To track end time

    public ScienceQuizGUI() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();

        if (!gd.isFullScreenSupported()) {
            System.err.println("Full-screen mode not supported");
            System.exit(0);
        }

        setTitle("Science Quiz");
        setUndecorated(true); // Remove window decorations
        setResizable(false); // Disable resizing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gd.setFullScreenWindow(this); // Set to full-screen mode

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

        add(mainPanel);

        setTitle("Science Quiz");

        // Front Page
        JPanel frontPanel = new JPanel();
        frontPanel.setLayout(new BoxLayout(frontPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("WELCOME to GOVT. ENG. COLLEGE, AJMER");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 60));
        frontPanel.add(titleLabel);
		
		
		// Add the title "Science Day Quiz" below the welcome message
		JLabel quizTitleLabel = new JLabel("Science Day Quiz");
		quizTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		quizTitleLabel.setFont(new Font("Times New Roman", Font.BOLD, 50));
		frontPanel.add(quizTitleLabel);
		// Load the image
		ImageIcon imageIcon = new ImageIcon("C:\\Users\\shari\\Pictures\\Picture1.jpg");
		Image image = imageIcon.getImage(); // transform it 
		Image newimg = image.getScaledInstance(250, 250,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
		imageIcon = new ImageIcon(newimg);  // transform it back
		JLabel imageLabel = new JLabel(imageIcon);
		imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment
		frontPanel.add(imageLabel);

        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 36));
        frontPanel.add(nameLabel);

        JTextField nameField = new JTextField(30);
        nameField.setMaximumSize(new Dimension(200, 30));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        frontPanel.add(nameField);

        JButton startButton = new JButton("Start Quiz");
		// Disable the Start Quiz button by default
		startButton.setEnabled(false);

startButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String playerNameInput = nameField.getText().trim(); // Get the trimmed text from the name field
        if (playerNameInput.isEmpty()) {
            JOptionPane.showMessageDialog(ScienceQuizGUI.this, "Please enter your name.", "Name Required", JOptionPane.WARNING_MESSAGE);
        } else {
            playerName = playerNameInput; // Store player's name
            remove(frontPanel); // Remove front page
            initializeQuiz(); // Initialize quiz after user confirms
            startButton.setEnabled(false); // Disable the start button after name is entered
        }
    }
});

// Add a DocumentListener to the name field to enable/disable the Start Quiz button
nameField.getDocument().addDocumentListener(new DocumentListener() {
    @Override
    public void insertUpdate(DocumentEvent e) {
        checkNameField();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        checkNameField();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        checkNameField();
    }

    private void checkNameField() {
        String playerNameInput = nameField.getText().trim();
        startButton.setEnabled(!playerNameInput.isEmpty());
    }
});

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size
        startButton.setPreferredSize(new Dimension(200, 60)); // Set preferred size
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = nameField.getText(); // Store player's name
                remove(frontPanel); // Remove front page
                initializeQuiz(); // Initialize quiz after user confirms
            }
        });
        frontPanel.add(startButton);

        add(frontPanel, BorderLayout.CENTER);
    }

	
    // Method to initialize the quiz interface
    private void initializeQuiz() {
        startTime = System.currentTimeMillis(); // Record start time
        questions = generateQuestions();
        Collections.shuffle(questions);

        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 42));
        add(questionLabel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS)); // Change layout to BoxLayout

        optionButtons = new JRadioButton[4];
        ButtonGroup optionGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 36));
            optionButtons[i].setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Add empty border for spacing
            optionGroup.add(optionButtons[i]);
            optionsPanel.add(optionButtons[i]);
        }

        add(optionsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 20)); // Set font size
        nextButton.setPreferredSize(new Dimension(200, 60)); // Set preferred size
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentQuestionIndex < questions.size() - 1) {
                    checkAnswer();
                    displayCorrectAnswer();
                    currentQuestionIndex++;
                    displayQuestion(currentQuestionIndex);
                } else {
                    checkAnswer();
                    showScore();
                }
            }
        });
        buttonPanel.add(Box.createVerticalStrut(50));
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        currentQuestionIndex = 0;
        score = 0;
        displayQuestion(currentQuestionIndex);
    }

    private void displayQuestion(int index) {
        Question currentQuestion = questions.get(index);
        JTextArea questionArea = new JTextArea(currentQuestion.getQuestion());
        questionArea.setFont(new Font("Times New Roman", Font.BOLD, 42));
        questionArea.setLineWrap(true); // Enable line wrapping
        questionArea.setWrapStyleWord(true); // Wrap at word boundaries
        questionArea.setEditable(false);
        questionArea.setBackground(this.getBackground()); // Match background color
        JScrollPane scrollPane = new JScrollPane(questionArea);
        scrollPane.setPreferredSize(new Dimension(900, 100)); // Adjust dimensions as needed
        add(scrollPane, BorderLayout.NORTH);
        ArrayList<String> options = currentQuestion.getOptions();
        Collections.shuffle(options);

        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(options.get(i));
            optionButtons[i].setSelected(false);
        }

        optionButtons[0].setSelected(true);
    }

    private void checkAnswer() {
        String selectedOption = "";
        for (int i = 0; i < 4; i++) {
            if (optionButtons[i].isSelected()) {
                selectedOption = optionButtons[i].getText();
                break;
            }
        }

        String correctOption = questions.get(currentQuestionIndex).getCorrectOption();
        if (selectedOption.equals(correctOption)) {
            score++;
        }
    }

    private void displayCorrectAnswer() {
        String correctAnswer = questions.get(currentQuestionIndex).getCorrectOption();
        String userAnswer = getUserAnswer();

        String message;
        if (userAnswer.equals(correctAnswer)) {
            message = "Yay, Your answer is correct! It's: " + correctAnswer;
        } else {
            message = "OOPS, wrong Answer. The right answer is: " + correctAnswer;
        }

        JOptionPane.showMessageDialog(this, message, "Answer Feedback", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getUserAnswer() {
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                return optionButtons[i].getText();
            }
        }
        return "";
    }

    private void showScore() {
        endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double totalTimeInSeconds = totalTime / 1000.0;

        StringBuilder feedbackMessage = new StringBuilder("Here are the correct answers:\n\n");

        for (int i = 0; i < questions.size(); i++) {
            Question currentQuestion = questions.get(i);
            String correctAnswer = currentQuestion.getCorrectOption();
            feedbackMessage.append("Question ").append(i + 1).append(": ").append(currentQuestion.getQuestion()).append("\n");
            feedbackMessage.append("Correct Answer: ").append(correctAnswer).append("\n\n");
        }

        JOptionPane.showMessageDialog(this, feedbackMessage.toString(), "Correct Answers", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(this, playerName + "'s Quiz Score: " + score + "\nTime Taken: " + totalTimeInSeconds + " seconds\nThank you for playing!", "Quiz Over", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }


    private ArrayList<Question> generateQuestions() {
    ArrayList<Question> generatedQuestions = new ArrayList<>();
    // Question 17
    String question17 = " In which year was the first National Science Day celebrated in India?";
    ArrayList<String> options17 = new ArrayList<>();
    options17.add("1971");
    options17.add("1980");
    options17.add("1985");
    options17.add("1995");
    Question q17 = new Question(question17, options17, "1995");
    generatedQuestions.add(q17);

    // Question 18
    String question18 = " Who is known as the father of Indian science?";
    ArrayList<String> options18 = new ArrayList<>();
    options18.add("C.V. Raman");
    options18.add("Jagdish Chandra Bose");
    options18.add("Homi J. Bhabha");
    options18.add("Vikram Sarabhai");
    Question q18 = new Question(question18, options18, "Jagdish Chandra Bose");
    generatedQuestions.add(q18);

    // Question 19
    String question19 = " Which famous Indian scientist discovered the Raman effect?";
    ArrayList<String> options19 = new ArrayList<>();
    options19.add("C.V. Raman");
    options19.add("Jagdish Chandra Bose");
    options19.add("Homi J. Bhabha");
    options19.add("Vikram Sarabhai");
    Question q19 = new Question(question19, options19, "C.V. Raman");
    generatedQuestions.add(q19);

    // Question 20
    String question20 = " The National Science Day is celebrated on which date every year in India?";
    ArrayList<String> options20 = new ArrayList<>();
    options20.add("28th February");
    options20.add("15th August");
    options20.add("26th January");
    options20.add("2nd October");
    Question q20 = new Question(question20, options20, "28th February");
    generatedQuestions.add(q20);

    // Question 21
    String question21 = " The theme of National Science Day 2024 is:";
    ArrayList<String> options21 = new ArrayList<>();
    options21.add("Future of STI: Impacts on Education, Skills and Work");
    options21.add("Science for a Sustainable Future");
    options21.add("Science for Sustainable Development and Aatmanirbhar Bharat");
    options21.add("Science and Technology for a Sustainable Future");
    Question q21 = new Question(question21, options21, "Science for a Sustainable Future");
    generatedQuestions.add(q21);

    // Question 22
    String question22 = " National Science Day is celebrated to mark the discovery of:";
    ArrayList<String> options22 = new ArrayList<>();
    options22.add("Raman effect");
    options22.add("Nuclear Fission");
    options22.add("Higgs Boson particle");
    options22.add("Gravity waves");
    Question q22 = new Question(question22, options22, "Raman effect");
    generatedQuestions.add(q22);

    // Question 23
    String question23 = "Who among the following was the first Indian to receive a Nobel Prize in Physics?";
    ArrayList<String> options23 = new ArrayList<>();
    options23.add("C.V. Raman");
    options23.add("Homi J. Bhabha");
    options23.add("Jagdish Chandra Bose");
    options23.add("Satyendra Nath Bose");
    Question q23 = new Question(question23, options23, "C.V. Raman");
    generatedQuestions.add(q23);

    // Question 24
    String question24 = " The Indian Space Research Organisation (ISRO) was established in which year?";
    ArrayList<String> options24 = new ArrayList<>();
    options24.add("1950");
    options24.add("1960");
    options24.add("1970");
    options24.add("1980");
    Question q24 = new Question(question24, options24, "1960");
    generatedQuestions.add(q24);

    // Question 25
    String question25 = " Who is known as the father of the Indian space program?";
    ArrayList<String> options25 = new ArrayList<>();
    options25.add("C.V. Raman");
    options25.add("Homi J. Bhabha");
    options25.add("Vikram Sarabhai");
    options25.add("Jagdish Chandra Bose");
    Question q25 = new Question(question25, options25, "Vikram Sarabhai");
    generatedQuestions.add(q25);

    // Question 26
    String question26 = " Who is considered as the first female engineer from India?";
    ArrayList<String> options26 = new ArrayList<>();
    options26.add("Anandi Gopal Joshi");
    options26.add("Kalpana Chawla");
    options26.add("Savitribai Phule");
    options26.add("Indira Nooyi");
    Question q26 = new Question(question26, options26, "Anandi Gopal Joshi");
    generatedQuestions.add(q26);

    // Question 27
    String question27 = "Who among the following is the founder of the Indian Institutes of  Technology (IITs)?";
    ArrayList<String> options27 = new ArrayList<>();
    options27.add("Homi J. Bhabha");
    options27.add("C.V. Raman");
    options27.add("Vikram Sarabhai");
    options27.add("Jawaharlal Nehru");
    Question q27 = new Question(question27, options27, "Homi J. Bhabha");
    generatedQuestions.add(q27);

    // Question 28
    String question28 = " The Indian Institute of Science (IISc) was established in which year?";
    ArrayList<String> options28 = new ArrayList<>();
    options28.add("1909");
    options28.add("1919");
    options28.add("1929");
    options28.add("1939");
    Question q28 = new Question(question28, options28, "1909");
    generatedQuestions.add(q28);

    // Question 29
    String question29 = "Who among the following was the first Indian to receive a Nobel Prize in Chemistry?";
    ArrayList<String> options29 = new ArrayList<>();
    options29.add("C.V. Raman");
    options29.add("Homi J. Bhabha");
    options29.add("Venkatraman Ramakrishnan");
    options29.add("Har Gobind Khorana");
    Question q29 = new Question(question29, options29, "Har Gobind Khorana");
    generatedQuestions.add(q29);

    // Question 30
    String question30 = "The acronym “Vigyan Jyoti” is associated with which initiative in India?";
    ArrayList<String> options30 = new ArrayList<>();
    options30.add("Promoting women’s education in science and technology");
    options30.add("Encouraging rural youth to pursue careers in science");
    options30.add("Popularizing science and technology among school children");
    options30.add("Developing scientific infrastructure in remote areas");
    Question q30 = new Question(question30, options30, "Promoting women’s education in science and technology");
    generatedQuestions.add(q30);

    // Question 31
    String question31 = "The Department of Science and Technology (DST) of the  Government of India was established in which year?";
    ArrayList<String> options31 = new ArrayList<>();
    options31.add("1947");
    options31.add("1951");
    options31.add("1971");
    options31.add("1981");
    Question q31 = new Question(question31, options31, "1951");
    generatedQuestions.add(q31);

    // Question 32
    String question32 = "Which famous Indian mathematician is known for his contribution to number theory, algebra, and infinite series?";
    ArrayList<String> options32 = new ArrayList<>();
    options32.add("Ramanujan");
    options32.add("Aryabhata");
    options32.add("Brahmagupta");
    options32.add("Bhaskara II");
    Question q32 = new Question(question32, options32, "Ramanujan");
    generatedQuestions.add(q32);

    // Question 33
    String question33 = "Who among the following is known for discovering the double-helix structure of DNA?";
    ArrayList<String> options33 = new ArrayList<>();
    options33.add("Rosalind Franklin");
    options33.add("James Watson");
    options33.add("Bhaskara II");
    options33.add("Maurice Wilkins");
    Question q33 = new Question(question33, options33, "James Watson");
    generatedQuestions.add(q33);

    // Question 34
    String question34 = "The acronym “INSPIRE” is associated with which initiative in India?";
    ArrayList<String> options34 = new ArrayList<>();
    options34.add("Promoting scientific research among students");
    options34.add("Providing scholarships to meritorious students");
    options34.add("Encouraging innovation and entrepreneurship");
    options34.add("Developing scientific infrastructure in remote areas");
    Question q34 = new Question(question34, options34, "Promoting scientific research among students");
    generatedQuestions.add(q34);

    // Question 35
    String question35 = "Which famous Indian scientist is known for his contribution to the  development of the Indian atomic energy program?";
    ArrayList<String> options35 = new ArrayList<>();
    options35.add("Homi J. Bhabha");
    options35.add("Vikram Sarabhai");
    options35.add("Satish Dhawan");
    options35.add("Raja Ramanna");
    Question q35 = new Question(question35, options35, "Homi J. Bhabha");
    generatedQuestions.add(q35);

	// Shuffle the list of questions
        Collections.shuffle(generatedQuestions);

    // Select only the first 5 questions
    ArrayList<Question> selectedQuestions = new ArrayList<>(generatedQuestions.subList(0, 5));

    return selectedQuestions;
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ScienceQuizGUI().setVisible(true);
            }
        });
    }
}

class Question {
    private String question;
    private ArrayList<String> options;
    private String correctOption;

    public Question(String question, ArrayList<String> options, String correctOption) {
        this.question = question;
        this.options = options;
        this.correctOption = correctOption;
    }

    public String getQuestion() {
        return question;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public String getCorrectOption() {
        return correctOption;
    }
}

