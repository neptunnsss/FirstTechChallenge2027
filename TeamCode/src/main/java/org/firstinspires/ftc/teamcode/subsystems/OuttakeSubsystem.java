package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class OuttakeSubsystem {
    private DcMotor outtakeMotor1;
    private DcMotor outtakeMotor2;

    public OuttakeSubsystem(HardwareMap hardwareMap) {
        outtakeMotor1 = hardwareMap.get(DcMotor.class, "OuttakeMotor1");
        outtakeMotor2 = hardwareMap.get(DcMotor.class, "OuttakeMotor2");

        // TODO: Если моторы шутера смотрят друг на друга, один из них скорее всего должен быть REVERSE
        outtakeMotor1.setDirection(DcMotor.Direction.FORWARD);
        outtakeMotor2.setDirection(DcMotor.Direction.REVERSE);

        outtakeMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        outtakeMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        outtakeMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        outtakeMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // TODO: Уточнить, нужны ли энкодеры
        // (Обычно для маховика шутера используют RUN_USING_ENCODER для поддержания постоянных RPM)
        outtakeMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        outtakeMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void setPower(double power) {
        outtakeMotor1.setPower(power);
        outtakeMotor2.setPower(power);
    }
}