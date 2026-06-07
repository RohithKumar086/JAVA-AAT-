package com.quizportal;

import com.quizportal.model.*;
import com.quizportal.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class QuizPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizPortalApplication.class, args);
    }

    @Bean
    CommandLineRunner seedData(
            UserRepository userRepo,
            QuizRepository quizRepo,
            QuestionRepository questionRepo,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // --- Seed Users ---
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@quiz.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);

            User alice = new User();
            alice.setUsername("alice");
            alice.setEmail("alice@quiz.com");
            alice.setPassword(passwordEncoder.encode("alice123"));
            alice.setRole(Role.STUDENT);
            userRepo.save(alice);

            User bob = new User();
            bob.setUsername("bob");
            bob.setEmail("bob@quiz.com");
            bob.setPassword(passwordEncoder.encode("bob123"));
            bob.setRole(Role.STUDENT);
            userRepo.save(bob);

            User rohan = new User();
            rohan.setUsername("rohan");
            rohan.setEmail("rohan@quiz.com");
            rohan.setPassword(passwordEncoder.encode("rohan123"));
            rohan.setRole(Role.STUDENT);
            userRepo.save(rohan);

            // --- Seed Quiz 1: Java Fundamentals ---
            Quiz javaQuiz = new Quiz();
            javaQuiz.setTitle("Java Fundamentals");
            javaQuiz.setDescription("Test your core Java knowledge — OOP, data types, and JVM basics.");
            javaQuiz.setTimeLimitMinutes(10);
            javaQuiz.setCategory("Programming");
            javaQuiz.setDifficulty(Difficulty.MEDIUM);
            javaQuiz.setCreatedBy(admin);
            javaQuiz.setActive(true);
            quizRepo.save(javaQuiz);

            List<Question> javaQs = Arrays.asList(
                new Question(null, "Which keyword is used to inherit a class in Java?", "extends", "implements", "inherits", "super", "A", javaQuiz),
                new Question(null, "What is the default value of an int in Java?", "null", "0", "-1", "undefined", "B", javaQuiz),
                new Question(null, "Which of these is NOT a primitive type in Java?", "int", "char", "String", "boolean", "C", javaQuiz),
                new Question(null, "What does JVM stand for?", "Java Variable Machine", "Java Virtual Machine", "Java Verified Module", "Just Virtual Memory", "B", javaQuiz),
                new Question(null, "Which method is the entry point of a Java program?", "start()", "run()", "main()", "init()", "C", javaQuiz),
                new Question(null, "What is the size of a long in Java?", "32 bits", "16 bits", "64 bits", "128 bits", "C", javaQuiz),
                new Question(null, "Which collection allows duplicate elements?", "Set", "HashMap", "ArrayList", "HashSet", "C", javaQuiz),
                new Question(null, "What is the parent class of all classes in Java?", "Base", "Object", "Class", "Root", "B", javaQuiz),
                new Question(null, "Which access modifier makes a member visible everywhere?", "private", "protected", "default", "public", "D", javaQuiz),
                new Question(null, "What does the 'final' keyword do to a variable?", "Makes it static", "Makes it constant", "Deletes it after use", "Makes it global", "B", javaQuiz)
            );
            questionRepo.saveAll(javaQs);

            // --- Seed Quiz 2: Machine Learning Basics ---
            Quiz mlQuiz = new Quiz();
            mlQuiz.setTitle("Machine Learning Basics");
            mlQuiz.setDescription("Foundational ML concepts — algorithms, terminology, and theory.");
            mlQuiz.setTimeLimitMinutes(8);
            mlQuiz.setCategory("AI/ML");
            mlQuiz.setDifficulty(Difficulty.HARD);
            mlQuiz.setCreatedBy(admin);
            mlQuiz.setActive(true);
            quizRepo.save(mlQuiz);

            List<Question> mlQs = Arrays.asList(
                new Question(null, "Which algorithm is used for classification and regression?", "K-Means", "PCA", "Decision Tree", "Apriori", "C", mlQuiz),
                new Question(null, "What does 'overfitting' mean?", "Model is too simple", "Model performs well on unseen data", "Model memorizes training data", "Model has no parameters", "C", mlQuiz),
                new Question(null, "Which activation function outputs values between 0 and 1?", "ReLU", "Sigmoid", "Tanh", "Softmax", "B", mlQuiz),
                new Question(null, "What is the purpose of a validation set?", "Training the model", "Tune hyperparameters", "Final evaluation", "Data augmentation", "B", mlQuiz),
                new Question(null, "Which loss function is used for binary classification?", "MSE", "MAE", "Cross-entropy", "Huber loss", "C", mlQuiz),
                new Question(null, "What does CNN stand for?", "Cyclic Neural Network", "Convolutional Neural Network", "Connected Node Network", "Central Net Node", "B", mlQuiz),
                new Question(null, "Which technique prevents overfitting by adding a penalty term?", "Dropout", "Regularization", "Normalization", "Pooling", "B", mlQuiz),
                new Question(null, "What is the role of backpropagation?", "Forward pass computation", "Update weights via gradients", "Initialize parameters", "Load training data", "B", mlQuiz)
            );
            questionRepo.saveAll(mlQs);

            // --- Seed Quiz 3: General Science ---
            Quiz scienceQuiz = new Quiz();
            scienceQuiz.setTitle("General Science");
            scienceQuiz.setDescription("Quick science trivia — physics, chemistry, and biology.");
            scienceQuiz.setTimeLimitMinutes(5);
            scienceQuiz.setCategory("Science");
            scienceQuiz.setDifficulty(Difficulty.EASY);
            scienceQuiz.setCreatedBy(admin);
            scienceQuiz.setActive(true);
            quizRepo.save(scienceQuiz);

            List<Question> sciQs = Arrays.asList(
                new Question(null, "What is the chemical symbol for water?", "WA", "H2O", "HO2", "W2O", "B", scienceQuiz),
                new Question(null, "What planet is known as the Red Planet?", "Venus", "Jupiter", "Mars", "Saturn", "C", scienceQuiz),
                new Question(null, "What is the speed of light approximately?", "3×10^6 m/s", "3×10^8 m/s", "3×10^10 m/s", "3×10^4 m/s", "B", scienceQuiz),
                new Question(null, "Which gas do plants absorb during photosynthesis?", "Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen", "C", scienceQuiz),
                new Question(null, "What is the atomic number of Carbon?", "4", "6", "8", "12", "B", scienceQuiz),
                new Question(null, "Newton's second law relates force to?", "Velocity", "Mass and acceleration", "Energy", "Momentum", "B", scienceQuiz)
            );
            questionRepo.saveAll(sciQs);

            System.out.println("✅ Quiz Portal seeded successfully!");
            System.out.println("👤 Demo accounts: admin/admin123 | alice/alice123 | bob/bob123 | rohan/rohan123");
        };
    }
}
