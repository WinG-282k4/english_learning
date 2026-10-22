# English Learning

Ứng dụng web học từ vựng tiếng Anh cá nhân. Frontend React + TypeScript, backend Spring Boot 3.5.16 + Java 25, dữ liệu H2 dạng file. Backend cung cấp REST API và có thể phục vụ bản frontend đã build trong cùng một container Docker.

## Trạng thái hiện tại

Đã có giao diện và API CRUD cho bài học, chủ đề và từ vựng. Quan hệ dữ liệu là **Lesson → Topic → Word**. Khi xóa bài học/chủ đề, backend xóa cả dữ liệu con. Moi topic co the chua khong gioi han so tu.

Đã triển khai học flashcard, kiểm tra hai lượt theo bài học/chủ đề và trang thống kê từ sai. Giao diện cài đặt AI cùng tích hợp gọi nhà cung cấp AI vẫn đang hoàn thiện.

## Nghiệp vụ và quy tắc

- Một bài học có nhiều chủ đề; một chủ đề có nhiều từ. Bài học tổng hợp từ các chủ đề qua truy vấn, không nhân bản từ.
- Moi topic can co it nhat mot tu khi tao lesson hoac import JSON; ung dung khong gioi han so tu trong topic. File JSON upload toi da 2 MB.
- Từ trùng giữa các chủ đề được cho phép. Thống kê từ sai dùng `term_norm` (chữ thường, bỏ khoảng trắng đầu/cuối), không phụ thuộc `word_id`.
- Bộ từ kiểm tra chủ đề gồm toàn bộ từ hiện tại, từ sai thuộc tối đa 3 topic đứng liền trước (3 topic gần nhất theo thứ tự trong lesson; không xét topic cũ hơn) và tối đa 3 từ sai ngẫu nhiên từ lịch sử; loại trùng theo `term_norm`.
- Bộ từ kiểm tra bài học gồm toàn bộ từ trong bài và tối đa 3 từ sai ngẫu nhiên ngoài bài.
- Bài kiểm tra có hai lượt: Anh → Việt, sau đó Việt → Anh. Ứng dụng chấm cục bộ và hiện phản hồi sau mỗi câu; AI chỉ được gọi khi câu trả lời lượt Anh → Việt chưa khớp. Nếu AI chưa cấu hình hoặc không khả dụng, câu trả lời được chấm theo các nghĩa đã nhập và người học có thể xác nhận lại.
- Kết quả hai lượt được lưu trong phiên kiểm tra; điểm là phần trăm câu đúng trên tổng số câu của cả hai lượt. Một từ được tính là đúng hoàn toàn khi đúng ở cả hai lượt.
- Từ đang sai được gỡ khỏi danh sách sai sau 3 câu đúng tích lũy. Nếu sai lại, bộ đếm đúng quay về 0.
- Thống kê được gộp theo `term_norm`: mỗi câu trả lời ở cả hai lượt làm tăng bộ đếm đúng hoặc sai tương ứng. Trang **Thống kê từ sai** lọc từ đang cần ôn/đã cải thiện, tìm theo từ và hiển thị lần sai gần nhất.
- Các ngưỡng 3 / 3 / 3 / 3 được cấu hình trong `app.rules` ở `application.yml`.
- JSON import hỗ trợ tạo bài học mới hoặc thêm topic và từ vào bài học đã chọn; dữ liệu được ghi trong một giao dịch.

## Thiết kế và cấu trúc

```text
FE/                         React, TypeScript, Vite
  src/pages/                Các trang giao diện
  src/services/             REST API clients
  src/types/                Kiểu dữ liệu frontend
BE/src/main/java/com/app/vocab/
  lesson, topic, word/      Entity, DTO, repository, service, controller
  domain/                   Logic nghiệp vụ thuần (pool, thống kê)
  test/, stat/               Kiểm tra và thống kê
  importer/                  Nhập bài học và nội dung từ JSON
  ai/, llm/                  API key, chấm AI và adapter nhà cung cấp
  config/                    Cấu hình Spring
BE/src/main/resources/
  application.yml            Cấu hình server, H2 và quy tắc
  prompts/                   Prompt AI
```

- Controller REST mỏng; logic dữ liệu nằm ở service và truy vấn Spring Data JPA.
- Entity `Topic` tham chiếu `Lesson`, `Word` tham chiếu `Topic`; không khai báo quan hệ ngược `OneToMany`.
- H2 lưu dữ liệu dạng file tại `./data/vocabdb`; Hibernate dùng `ddl-auto: update`.
- Frontend dùng `fetch` qua `FE/src/services/apiClient.ts`, API có tiền tố `/api`.
- Docker dùng ba giai đoạn: build frontend, build Spring Boot, chạy JAR bằng Java 25.

### API CRUD hiện có

| Đối tượng | Endpoint |
| --- | --- |
| Bài học | `GET/POST /api/lessons`, `GET/PUT/DELETE /api/lessons/{id}`, `POST /api/lessons/with-content`, `POST /api/lessons/{id}/import-json` |
| Chủ đề | `GET /api/topics?lessonId={id}`, `GET/POST /api/topics`, `GET/PUT/DELETE /api/topics/{id}` |
| Từ | `GET /api/words?topicId={id}`, `GET /api/words/by-lesson/{id}`, `POST /api/words`, `POST /api/words/batch`, `PUT/DELETE /api/words/{id}` |
| Thống kê | `GET /api/stats/wrong-words?limit=100` (tối đa 200 từ, sắp theo số lượt sai) |

Tạo/sửa bài học gửi JSON `{ "title": "Tên bài học" }`. Tạo/sửa chủ đề gửi `{ "lessonId": 1, "title": "Tên chủ đề", "orderIndex": 0 }`. Tạo/sửa từ gửi `{ "topicId": 1, "term": "apple", "pos": "noun", "ipa": "ˈæp.əl", "meaningVi": "quả táo", "example": "I eat an apple." }` (`pos`, `ipa`, `example`, `orderIndex` có thể bỏ qua).

Trong trang chi tiết topic, form nhập từ chỉ mở khi nhấn **Thêm từ**. Có thể thêm nhiều dòng và lưu một lần; backend kiểm tra tổng số từ của topic khong gioi han so tu và lưu cả batch trong một giao dịch.

Trên trang Bài học chọn **Thêm bài học** để mở form tạo lesson, topic và words cùng lúc; có thể nhập tay hoặc nạp JSON để điền form, chỉnh sửa rồi lưu một lần. File JSON tối đa 2 MB; có file mẫu tại `examples/example.json`. Dùng cấu trúc:

```json
{
  "title": "Đồ ăn",
  "topics": [
    {
      "title": "Trái cây",
      "words": [
        { "term": "apple", "pos": "noun", "ipa": "/ˈæp.əl/", "meaningVi": "quả táo", "example": "I eat an apple." }
      ]
    }
  ]
}
```

When importing into an existing lesson, include at least one topic and at least one word per topic. Each word needs `term` and `meaningVi`. There is no per-topic word limit. JSON uploads are limited to 2 MB. Import appends topics without replacing existing content.

## Cách chạy bằng Docker (khuyến nghị)

Cần cài Docker Desktop có Docker Compose. Chạy từ thư mục gốc dự án.

PowerShell:

```powershell
$env:SECRET_KEY = 'thay-bang-chuoi-bi-mat-rieng-dai-tu-32-ky-tu'
docker compose up --build
```

Mở <http://localhost:8080>. API ở `/api`. Dữ liệu H2 được lưu trong thư mục `data/` nhờ volume Compose. Dừng bằng `Ctrl+C`; chạy nền với `docker compose up --build -d` và dừng bằng `docker compose down`.

`SECRET_KEY` dùng cho khóa mã hóa AI. Không dùng giá trị mặc định trong môi trường thật và không commit khóa thật vào Git. Compose bind cổng vào `127.0.0.1` để chỉ truy cập từ máy hiện tại.

## Chạy frontend và backend riêng khi phát triển

Cần Node.js 20+, npm, JDK 25 và Maven 3.10+.

1. Terminal thứ nhất, chạy backend:

   ```powershell
   cd BE
   $env:SECRET_KEY = 'thay-bang-chuoi-bi-mat-rieng-dai-tu-32-ky-tu'
   mvn spring-boot:run
   ```

2. Terminal thứ hai, cài gói và chạy frontend:

   ```powershell
   cd FE
   npm install
   npm run dev
   ```

Mở URL Vite in ra trong terminal (thường là <http://localhost:5173>). `FE/vite.config.ts` chuyển tiếp các request `/api` đến backend ở `http://localhost:8080`.

## Build và kiểm thử

```powershell
cd FE
npm install
npm run build
npm test -- --run

cd ../BE
mvn test
mvn package
```

Frontend build tạo `FE/dist`; Maven tạo JAR trong `BE/target`. Dockerfile tự chạy hai bước build khi `docker compose up --build`.

## Cấu hình

- Cấu hình Spring/H2/quy tắc: `BE/src/main/resources/application.yml`.
- `SECRET_KEY`: biến môi trường; giá trị dự phòng chỉ dành cho chạy local thử nghiệm.
- Frontend gọi cùng host qua `/api`; trong Docker backend vừa cung cấp API vừa phục vụ static frontend.

## Giới hạn hiện tại

Giao diện lưu/quản lý API key và tích hợp gọi Gemini/Groq chưa hoàn thiện; vì vậy AI chấm câu trả lời hiện trả về không khả dụng. Bài kiểm tra vẫn chạy với bộ chấm cục bộ và cho phép người học xác nhận đáp án.
