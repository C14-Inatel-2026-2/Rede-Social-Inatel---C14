package chatzone;

public final class Session {
    private static String email;
    private static String nome;

    private Session() {}

    public static void setUser(String userEmail, String userName) {
        email = userEmail;
        nome = userName;
    }

    public static String email() { return email; }
    public static String nome() { return nome; }
    public static void clear() { email = null; nome = null; }
}
