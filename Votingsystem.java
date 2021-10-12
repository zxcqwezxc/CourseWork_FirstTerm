import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Votingsystem {

    static ArrayList<String> winners = new ArrayList<String>();
    static LinkedList<String> commands = new LinkedList<>();

    public static void main(String[] args) throws Exception {
        final String regUser = "reg-user";
        final String admin = "admin";
        final String help = "help";
        final String end = "end";
        final String vote = "vote";
        final String yes = "yes";
        final String no = "no";
        Scanner in = new Scanner(System.in);
        boolean check = false;
        boolean needReadLog = false;
        String logFilename = null;
        int count = 0;

        if (args.length > 0 && (args[0].equals("-f") || args[0].equals("--file"))) {
            needReadLog = true;
            logFilename = args[1];
        }

        Deque<String> startCommands = new LinkedList<>();
        if (needReadLog) {
            try (BufferedReader reader = new BufferedReader(new FileReader(logFilename))) {
                String s;
                while ((s = reader.readLine()) != null) {
                    startCommands.add(s);
                }
            } catch (IOException ex) {

                System.out.println(ex.getMessage());
            }
        }

        if (needReadLog) {
            count = startCommands.size();
        }

        System.out.print(
                "reg-user - регистрация пользователя\nadmin - выполнение операций, которые доступны администратору\nhelp - вывести список команд\nend - завершение работы\nvote - голосовать\n");
        boolean endswitch = false;
        String command = "";
        do {
            System.out.println("Главное меню\nНапишите help для вызова списка команд\nВведите команду:\n");
            check = false;

            do {
                if (needReadLog && count != 0) {
                    command = startCommands.pollFirst();
                    count--;
                }
                if (count == 0) {
                    command = in.nextLine();
                }
                if (!command.equalsIgnoreCase(end)) {
                    commands.add(command);
                }
                if (command.equalsIgnoreCase(regUser) || command.equalsIgnoreCase(admin)
                        || command.equalsIgnoreCase(help) || command.equalsIgnoreCase(end)
                        || command.equalsIgnoreCase(vote)) {
                    check = true;
                } else {
                    check = false;
                    System.out.println("Вы ввели неправильную команду, попробуйте снова");
                }
            } while (!check);

            final String regCandidate = "reg-candidate";
            final String candidate = "candidate";
            String admincommand = "";

            switch (command) {

                case regUser:
                    Reguser.registration(in);
                    System.out.println("\n");
                    break;
                case vote:
                    if (User.enter(in) == true && User.isVoted() == false) {
                        User.vote(in);
                    }
                    System.out.println("\n");
                    break;
                case admin:
                    System.out.println("Команды администратора:\nreg-candidate - зарегистрировать кандидата"
                    + "\ncandidate - вывести голоса кандидата\n");
                    admincommand = in.nextLine();
                    if (!admincommand.equalsIgnoreCase(end)) {
                        commands.add(admincommand);
                    }
                    if (admincommand.equalsIgnoreCase(regCandidate)) {
                        Regcandidate.registercandidate(in);
                        System.out.println("\n");
                        break;
                    }
                    if (admincommand.equalsIgnoreCase(end)) {
                        break;
                    }
                    if (admincommand.equalsIgnoreCase(candidate)) {
                        Candidate.getLoginandPassword(in);
                        System.out.println("\n");
                        break;
                    } else {
                        System.out.println("Вы ввели неправильную команду\n");
                        break;
                    }
                case help:
                    System.out.println("reg-user - регистрация пользователя\nadmin - выполнение операций, которые доступны администратору\nhelp - вывести список команд\nend - завершение работы\nvote - голосовать\n");
                    break;
                case end:
                    check = false;
                    do {
                        System.out.println("Вы хотите выйти и сохранить список всех введённых команд?(yes/no)\n");
                        String endcommand = "";
                        endcommand = in.nextLine();
                        commands.add(endcommand);
                        if (endcommand.equalsIgnoreCase(yes)) {
                            DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss");
                            File myFile = new File(
                                    "result_" + timeStampPattern.format(java.time.LocalDateTime.now()) + ".txt");
                            if (!commands.isEmpty()) {
                                try (FileWriter writer = new FileWriter(myFile, false)) {
                                    writer.append(commands.get(0));
                                    for (int i = 1; i < commands.size(); ++i) {
                                        writer.append('\n').append(commands.get(i));
                                    }
                                    writer.flush();
                                } catch (IOException ex) {

                                    System.out.println(ex.getMessage());
                                }
                            }
                            check = true;
                            endswitch = true;
                            break;
                        }
                        if (endcommand.equalsIgnoreCase(no)) {
                            check = true;
                            break;
                        } else {
                            check = false;
                            System.out.println("Вы ввели неправильную команду, повторите ввод.\n");
                        }
                    } while (!check);

                default:
                    break;
            }
        } while (!endswitch);

        int winningValue = 0;
        String winner = "";
        boolean oneWinner = true;

        for (HashMap.Entry<String, Integer> entry : Regcandidate.loginsandvotes.entrySet()) {
            String key = entry.getKey();
            int value = (int) entry.getValue();
            if (value > winningValue) {
                winningValue = value;
                winner = key;
            }
        }

        winners.add(winner + " ");
        Regcandidate.loginsandvotes.remove(winner);

        for (HashMap.Entry<String, Integer> entry : Regcandidate.loginsandvotes.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value == winningValue) {
                winners.add(key + " ");
                oneWinner = false;
            }
        }

        if (oneWinner == true) {
            System.out.println("Победитель: " + winner + "\n");
        } else {
            System.out.println("Победители: " + getArrayList() + "\n");
        }
        in.close();
    }

    public static ArrayList<String> getArrayList() {
        return winners;
    }
}