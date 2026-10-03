package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class ServoSubsystem {

    Servo servo;
    CRServo crServo;
    public ServoSubsystem(HardwareMap hardwareMap) {
        servo = hardwareMap.get(Servo.class, "servo name");
        crServo = hardwareMap.get(CRServo.class, "crServo name");

    }
    public void setServoPosition(double position) {
        servo.setPosition(position);
    }
    public void setCRServoPosition(double position) {
        crServo.setPower(position);
    }

}
