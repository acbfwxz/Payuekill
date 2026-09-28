# พายุจัดการไวรัสโหด

โปรเจกต์ Android สำหรับ sideload/build เอง

ฟีเจอร์:
- ตรวจแอปที่ติดตั้ง
- ตรวจไฟล์ใน external storage เมื่อได้รับสิทธิ์ MANAGE_EXTERNAL_STORAGE
- ตรวจชื่อไฟล์/แพ็กเกจ/รายการใน APK ด้วย heuristic
- UI ภาษาไทย

ข้อจำกัด:
- ไม่ใช่ commercial antivirus และไม่รับประกันการตรวจพบมัลแวร์ทุกชนิด
- ไม่ลบไฟล์อัตโนมัติ เพื่อป้องกัน false positive
- การใช้ MANAGE_EXTERNAL_STORAGE มีข้อจำกัดของ Google Play; เหมาะกับการติดตั้งเอง/ทดสอบ

Build:
เปิดด้วย Android Studio แล้ว Build > Build APK(s)
