package org.firstinspires.ftc.teamcode.teleops;


import static java.lang.Double.max;
import static java.lang.Double.min;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp (name = "firstteleop", group = "TeleOp")
public class firstteleop extends LinearOpMode {
    private CRServo servo;
    private DcMotor FleftMotor;
    private DcMotor FrightMotor;
    private DcMotor BleftMotor;
    private DcMotor BrightMotor;
    private DcMotor IntakeMotor;
    private DcMotor OuttakeMotor1;
    private DcMotor OuttakeMotor2;
    private ElapsedTime runtime = new ElapsedTime();


    private boolean stateM = false;
    private double kp = 1;
    private double ki = 1;
    private double kd = 1;
    private double intsum = 0;
    private double lastError = 0;

    public void runOpMode() {
        telemetry.addData("Статус", "Инициализация...");
        telemetry.update();

        initializeHardware();

        telemetry.addData("Статус", "Инициализация завершена");
        telemetry.addData("Управление", "Левый джойстик - движение");
        telemetry.addData("Управление", "Правый джойстик X - повороты");
        telemetry.addData("Управление", "Left Bumper - медленный режим");
        telemetry.update();

        while (!isStarted() && !isStopRequested()) {
            telemetry.addData("Статус", "Ожидание старта...");
            telemetry.update();
        }

        runtime.reset();

        while (opModeIsActive()) {
            controlDrive();
            displayTelemetry();
            Intake();
            Outtake();
        }
    }

    private void initializeHardware() {
        servo = hardwareMap.get(CRServo.class, "servo");
        FleftMotor = hardwareMap.get(DcMotor.class, "FleftMotor");
        FrightMotor = hardwareMap.get(DcMotor.class, "FrightMotor");
        BleftMotor = hardwareMap.get(DcMotor.class, "BleftMotor");
        BrightMotor = hardwareMap.get(DcMotor.class, "BrightMotor");
        IntakeMotor = hardwareMap.get(DcMotor.class, "IntakeMotor");
        OuttakeMotor1 = hardwareMap.get(DcMotor.class, "OuttakeMotor1");
        OuttakeMotor2 = hardwareMap.get(DcMotor.class, "OuttakeMotor2");

        FleftMotor.setDirection(DcMotor.Direction.FORWARD);
        FrightMotor.setDirection(DcMotor.Direction.REVERSE);
        BleftMotor.setDirection(DcMotor.Direction.FORWARD);
        BrightMotor.setDirection(DcMotor.Direction.REVERSE);
        IntakeMotor.setDirection(DcMotor.Direction.FORWARD);
        OuttakeMotor1.setDirection(DcMotor.Direction.FORWARD);
        OuttakeMotor2.setDirection(DcMotor.Direction.REVERSE);

        FleftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FrightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BleftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BrightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        IntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        OuttakeMotor1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        OuttakeMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        FleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        FrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        BrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        IntakeMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        OuttakeMotor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        OuttakeMotor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        FleftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FrightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BleftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BrightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        IntakeMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        OuttakeMotor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        OuttakeMotor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private void controlDrive() {
        double drive = gamepad1.left_stick_y;
        double strafe = -gamepad1.left_stick_x;
        double turn = -gamepad1.right_stick_x;

        double speedMultiplier = gamepad1.left_bumper ? 0.5 : 0.8;

        double FleftPower = (drive + strafe + turn) * speedMultiplier;
        double FrightPower = (drive - strafe - turn) * speedMultiplier;
        double BleftPower = (drive - strafe + turn) * speedMultiplier;
        double BrightPower = (drive + strafe - turn) * speedMultiplier;

        FleftMotor.setPower(FleftPower);
        FrightMotor.setPower(FrightPower);
        BleftMotor.setPower(BleftPower);
        BrightMotor.setPower(BrightPower);
    }

    private void displayTelemetry() {
        telemetry.addData("Статус", "Робот активен");
        telemetry.addData("Время работы", "%.1f сек", runtime.seconds());
        telemetry.addData("", "");

        telemetry.addData("Левый джойстик", "Y: %.2f", gamepad1.left_stick_y);
        telemetry.addData("Левый джойстик", "X: %.2f", gamepad1.left_stick_x);
        telemetry.addData("Правый джойстик", "X: %.2f", gamepad1.right_stick_x);
        telemetry.addData("Правый джойстик", "Y: %.2f", gamepad1.right_stick_y);
        telemetry.addData("Медленный режим", gamepad1.left_bumper);

        telemetry.addData("","");
        telemetry.addData("FL мощность", "%.2f", FleftMotor.getPower());
        telemetry.addData("FR мощность", "%.2f", FrightMotor.getPower());
        telemetry.addData("BL мощность", "%.2f", BleftMotor.getPower());
        telemetry.addData("BR мощность", "%.2f", BrightMotor.getPower());

        telemetry.update();
    }

    private void Outtake() {
        double outTakePower = 0;
        if (gamepad1.right_bumper) {
            outTakePower = 0.9;
        } else if (gamepad1.left_bumper) {
            outTakePower = 0.5;
        }
        OuttakeMotor1.setPower(outTakePower);
        OuttakeMotor2.setPower(outTakePower);
    }

    private void Intake() {
        double intakePower = 0;
        if (gamepad1.right_trigger > 0.1) {
            intakePower = min(0.8, gamepad1.right_trigger);
        } else if (gamepad1.left_trigger > 0.1) {
            intakePower = max(-0.8, -gamepad1.left_trigger);
        }
        IntakeMotor.setPower(intakePower);
    }
}
