import java.util.UUID;

public class Test {
    @org.junit.jupiter.api.Test
    public void test(){
        System.out.println(UUID.randomUUID().toString().replaceAll("-",""));
        System.out.println("新加的");
    }
}
