const {onValueCreated} = require("firebase-functions/v2/database");
const {logger} = require("firebase-functions");
const admin = require("firebase-admin");
const mailgun = require("mailgun-js");

// Initialize Firebase Admin SDK
admin.initializeApp();

// Configure Mailgun
const mg = mailgun({
  apiKey: "4a29fa132c9a46b74075a2286eab9b1a-2e68d0fb-19bcd53f",
  domain: "sandboxc8e77e08934b4291af4f7fda47b6c041.mailgun.org",
});

exports.handleNewExam = onValueCreated(
    {
      ref: "/exams/{examId}",
      region: "asia-southeast1",
    },
    async (event) => {
      try {
        const examData = event.data.val();
        const examId = event.params.examId;

        if (!examData) {
          logger.error("No exam data found");
          return;
        }

        logger.log("New exam detected:", examId, examData);

        // Kiểm tra class name
        const className = examData.class;
        if (!className) {
          logger.error("Class name is missing in the exam data");
          return;
        }

        // Lấy thông tin lớp học từ database
        const db = admin.database();
        const classRef = db.ref(`Classes/${className}`);
        const classSnapshot = await classRef.once("value");
        const classData = classSnapshot.val();

        if (!classData || !classData.students) {
          logger.error(`No students found in class ${className}`);
          return;
        }

        // Gửi email cho từng học sinh
        const emailPromises = Object.entries(classData.students)
            .filter(([_, student]) => student.email)
            .map(([_, student]) => {
              const emailData = {
                from: "E-Exam System <no-reply@eexam.com>",
                to: student.email,
                subject: `Bài kiểm tra mới: ${examData.name}`,
                text: `
Xin chào ${student.studentName},

Một bài kiểm tra mới đã được tạo trong lớp ${className} của bạn.

Chi tiết bài kiểm tra:
- Tên: ${examData.name}
- Thời hạn: ${new Date(examData.deadline).toLocaleString("vi-VN")}

Vui lòng đăng nhập vào hệ thống để làm bài.

Trân trọng,
Đội ngũ E-Exam
            `,
              };

              return mg.messages().send(emailData)
                  .catch((error) => {
                    logger.error(`Failed to send to ${student.email}:`, error);
                    return null;
                  });
            });

        await Promise.allSettled(emailPromises);
        logger.log(`Notification process completed for exam ${examData.name}`);
      } catch (error) {
        logger.error("Error in handleNewExam:", error);
      }
    },
);
