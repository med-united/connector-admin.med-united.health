package health.medunited.architecture.monitoring;

import java.io.IOException;

public class Monitoring {

    public int age;
    public int height;

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public Monitoring (int age, int height) throws IOException {
        this.age = age;
        this.height = height;

        System.out.println("writing file");


    }

}

