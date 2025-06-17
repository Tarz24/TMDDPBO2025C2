# Cosmic Skill Collector

![Java](https://img.shields.io/badge/language-Java-orange.svg)
![Platform](https://img.shields.io/badge/platform-Swing%20GUI-blue.svg)
![Build](https://img.shields.io/badge/build-Maven-red.svg)
![Database](https://img.shields.io/badge/database-SQLite-blue.svg)

**Cosmic Skill Collector** adalah game 2D berbasis Java Swing yang menantang pemain untuk mengendalikan seorang astronot dalam misi mengumpulkan "Skill Balls" di luar angkasa. Dengan *gameplay* yang dinamis, sistem skor, dan leaderboard yang persisten, game ini dibangun untuk menguji refleks dan ketangkasan pemain.

Proyek ini merupakan implementasi dari Tugas Masa Depan Individu untuk mata kuliah Desain dan Pemrograman Berorientasi Objek, yang didesain menggunakan arsitektur **MVVM (Model-View-ViewModel)** dan dikelola menggunakan **Maven**.

[Gameplay Screenshot]
![Image](https://github.com/user-attachments/assets/ca235d74-abcc-4be4-a529-3526f6e04911)

![Image](https://github.com/user-attachments/assets/a45f5f23-df18-4768-bf9f-dc8714898f15)

## Daftar Isi
- [Fitur Utama](#fitur-utama)
- [Arsitektur Proyek](#arsitektur-proyek)
- [Teknologi yang Digunakan](#teknologi-yang-digunakan)
- [Struktur Proyek](#struktur-proyek)
- [Panduan Instalasi & Menjalankan](#panduan-instalasi--menjalankan)
- [Cara Bermain](#cara-bermain)
- [Kredit dan Atribusi](#kredit-dan-atribusi)
- [Lisensi](#lisensi)

## Fitur Utama
- **Gameplay Dinamis**: Objek "Skill Ball" muncul secara acak dari berbagai arah dengan nilai dan kecepatan yang berbeda-beda.
- **Sistem Skor & Peringatan**: Kumpulkan bola untuk mendapatkan skor. Hindari objek berbahaya (Pluto) yang akan menambah jumlah peringatan. Game berakhir jika batas peringatan tercapai.
- **Leaderboard Persisten**: Skor tertinggi disimpan secara lokal menggunakan database SQLite, memungkinkan pemain untuk bersaing memperebutkan peringkat teratas di "Hall of Fame".
- **Kontrol Responsif**:
    - Gerakkan pemain menggunakan tombol `W, A, S, D` atau `Tombol Panah`.
    - Tangkap bola dengan `Klik Kiri Mouse`.
- **Antarmuka Modern**: UI yang dirancang dengan efek visual seperti *glass morphism*, gradien, dan animasi untuk memberikan pengalaman yang menarik.
- **Efek Suara & Musik Latar**: Dilengkapi dengan musik latar yang memacu adrenalin dan efek suara untuk setiap aksi penting dalam game.
- **Arsitektur MVVM**: Kode diorganisir dengan rapi menggunakan pola desain MVVM untuk memisahkan logika data, tampilan, dan logika bisnis.

## Arsitektur Proyek
Proyek ini mengadopsi pola arsitektur **Model-View-ViewModel (MVVM)** untuk memastikan kode yang bersih, terorganisir, dan mudah dikelola.

- **Model**: Berisi kelas-kelas data murni yang merepresentasikan objek dalam game.
  - `Ball.java`, `PlayerResult.java`, `FloatingText.java`, `Net.java`.
- **View**: Bertanggung jawab untuk menampilkan antarmuka pengguna (UI) dan menerima input dari pengguna. View "mengamati" perubahan pada ViewModel.
  - `MainMenuView.java`, `GameView.java`.
- **ViewModel**: Bertindak sebagai jembatan antara Model dan View. ViewModel berisi semua logika tampilan dan *state management*, serta memberi tahu View kapan harus memperbarui dirinya.
  - `MainMenuViewModel.java`, `GameViewModel.java`.
- **Repository**: Bertugas sebagai abstraksi untuk sumber data. Dalam proyek ini, ia mengelola semua operasi ke database SQLite.
  - `DatabaseRepository.java`.
- **Service**: Menyediakan fungsionalitas spesifik yang dapat digunakan di seluruh aplikasi, seperti manajemen suara.
  - `SoundService.java`.

## Teknologi yang Digunakan
- **Bahasa**: [Java](https://www.java.com/)
- **Framework GUI**: [Java Swing](https://docs.oracle.com/javase/tutorial/uiswing/)
- **Build & Dependency Management**: [Apache Maven](https://maven.apache.org/)
- **Database**: [SQLite](https://www.sqlite.org/) via [sqlite-jdbc](https://github.com/xerial/sqlite-jdbc)

## Struktur Proyek
Berikut adalah struktur direktori utama dari proyek `CollectTheSkillBalls`:
```
CollectTheSkillBalls/
├── pom.xml                 (File konfigurasi Maven, jika ada)
├── lib/
│   └── sqlite-jdbc-3.49.1.0.jar
├── resources/
│   ├── database/
│   │   └── skillballs.db
│   ├── images/             (Aset gambar seperti asteroid.png, astronaut.png, dll.)
│   └── sounds/             (Aset suara seperti catch.wav, music.wav)
├── src/
│   ├── Main.java           (Entry point aplikasi)
│   ├── models/             (Contoh: Ball.java, PlayerResult.java)
│   ├── repositories/       (Contoh: DatabaseRepository.java)
│   ├── services/           (Contoh: SoundService.java)
│   ├── viewmodels/         (Contoh: GameViewModel.java, MainMenuViewModel.java)
│   └── views/              (Contoh: GameView.java, MainMenuView.java)
└── README.md
```

## Panduan Instalasi & Menjalankan (Menggunakan Command Prompt)

Bagian ini menjelaskan cara mengkompilasi dan menjalankan proyek `CollectTheSkillBalls` menggunakan Command Prompt (atau terminal serupa seperti PowerShell) tanpa memerlukan Apache Maven.

### Prasyarat
1.  **JDK (Java Development Kit)**: Pastikan Anda memiliki JDK versi 11 atau yang lebih baru terinstal. Perintah `javac` (untuk kompilasi) dan `java` (untuk menjalankan) harus dapat diakses dari terminal Anda.
2.  **Git**: Diperlukan jika Anda ingin men-clone repositori untuk mendapatkan kode sumber terbaru.
3.  **File Proyek**: Anda memerlukan seluruh struktur proyek `CollectTheSkillBalls`, termasuk folder `src`, `lib`, dan `resources`.

### Langkah-Langkah Instalasi dan Menjalankan

1.  **Dapatkan Kode Proyek**:
    * **Jika menggunakan Git untuk clone**:
        Buka terminal, navigasi ke direktori tempat Anda ingin menyimpan proyek (misalnya `D:\UPI\Semester 4\Desain dan Pemrograman Berorientasi Objek\TMD`), lalu jalankan:
        ```sh
        git clone [https://github.com/Tarz24/TMDDPBO2025C2.git](https://github.com/Tarz24/TMDDPBO2025C2.git)
        cd TMDDPBO2025C2
        cd CollectTheSkillBalls
        ```
    * **Jika sudah memiliki file proyek**:
        Buka terminal dan navigasi hingga Anda berada di dalam direktori root proyek `CollectTheSkillBalls`. Misalnya:
        ```cmd
        cd "D:\UPI\Semester 4\Desain dan Pemrograman Berorientasi Objek\TMD\CollectTheSkillBalls"
        ```

2.  **Buka Command Prompt/Terminal di Direktori Proyek `CollectTheSkillBalls`**
    **SANGAT PENTING:** Pastikan terminal Anda aktif di dalam direktori `CollectTheSkillBalls` (misalnya, prompt Anda menunjukkan `D:\UPI\Semester 4\Desain dan Pemrograman Berorientasi Objek\TMD\CollectTheSkillBalls>`). Semua perintah berikutnya dijalankan dari sini.

3.  **Buat Direktori Output (Opsional tapi Direkomendasikan)**
    Direktori ini akan menyimpan file hasil kompilasi (`.class`).
    ```cmd
    mkdir bin
    ```

4.  **Kompilasi Kode Sumber**
    Gunakan perintah `javac` berikut untuk mengkompilasi `Main.java` dan semua kelas lain yang direferensikannya dari folder `src`, menempatkan file `.class` yang dihasilkan ke dalam folder `bin`.
    ```cmd
    javac -d bin -cp "lib/sqlite-jdbc-3.49.1.0.jar" -sourcepath src src/Main.java
    ```

5.  **Jalankan Aplikasi**
    Setelah kompilasi berhasil, jalankan aplikasi menggunakan perintah `java`.
    ```cmd
    java -cp "bin;lib/sqlite-jdbc-3.49.1.0.jar;resources" Main
    ```
    *Catatan:*
    * `Main` adalah nama kelas yang berisi metode `public static void main(String[] args)`.
    * Pemisah classpath di Windows adalah titik koma (`;`). Jika Anda menggunakan terminal berbasis Unix (seperti Git Bash di Windows atau di Linux/macOS), gunakan titik dua (`:`).
    * Folder `resources` ditambahkan ke classpath agar aplikasi dapat menemukan aset seperti gambar, suara, dan file `skillballs.db`.

### Menjalankan di Visual Studio Code (Tanpa Maven)

Jika Anda ingin menjalankan proyek ini dari Visual Studio Code tanpa menggunakan Maven, Anda perlu:
1.  Memastikan Anda telah menginstal [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) dari Microsoft.
2.  Membuka folder `CollectTheSkillBalls` sebagai root workspace di VS Code.
3.  Mengkompilasi kode secara manual terlebih dahulu menggunakan Command Prompt seperti yang dijelaskan pada Langkah 4 di atas.
4.  Mengkonfigurasi file `launch.json` untuk menjalankan aplikasi.
    * Buka file `src/Main.java`.
    * Pergi ke tampilan "Run and Debug" (`Ctrl+Shift+D`).
    * Jika belum ada `launch.json`, klik "create a launch.json file" dan pilih "Java".
    * Edit file `launch.json` yang dibuat agar terlihat seperti ini:

    ```json
    {
        "version": "0.2.0",
        "configurations": [
            {
                "type": "java",
                "name": "Launch Main (CollectTheSkillBalls)",
                "request": "launch",
                "mainClass": "Main",
                "projectName": "CollectTheSkillBalls",
                "classPaths": [
                    "${workspaceFolder}/bin",
                    "${workspaceFolder}/lib/sqlite-jdbc-3.49.1.0.jar",
                    "${workspaceFolder}/resources"
                ],
                "vmArgs": ""
            }
        ]
    }
    ```

Setelah `launch.json` dikonfigurasi dan kode telah dikompilasi ke folder `bin`, Anda dapat memilih konfigurasi "Launch Main (CollectTheSkillBalls)" dan menekan tombol play (F5).

## Cara Bermain
1.  **Masukkan Nama**: Pada menu utama, ketik nama pengguna Anda.
2.  **Mulai Misi**: Klik tombol `🚀 LAUNCH MISSION` untuk memulai permainan.
3.  **Navigasi**: Gunakan tombol `W, A, S, D` atau `Tombol Panah` untuk menggerakkan astronot.
4.  **Tangkap Bola**: Arahkan kursor mouse ke objek dan **klik kiri** untuk menembakkan jaring dan menangkapnya.
5.  **Kumpulkan Skor**: Setiap objek memiliki nilai yang berbeda. Objek **Pluto** bernilai negatif dan akan menambah `PLUTO HITS`.
6.  **Hindari Bahaya**: Permainan akan berakhir jika Anda mengenai Pluto sebanyak **30 kali**.
7.  **Selesaikan Misi**: Anda bisa menekan tombol `SPACE` untuk mengakhiri permainan kapan saja dan menyimpan skor Anda.

## Kredit dan Atribusi
Semua aset visual dan audio yang digunakan dalam proyek ini adalah milik kreator aslinya.

#### 🎨 Aset Visual
- **Background**: Pinterest Space Collection
- **Star, Net, Space-Shuttle, UFO**: Freepik - Flaticon
- **Asteroid**: VectorPortal - Flaticon
- **Astronaut**: iconfield - Flaticon
- **Meteorite**: Vitaly Gorbachev - Flaticon
- **Pluto**: Peerapak Takpho - Flaticon
- **Satellite**: Muhammad Atif - Flaticon

#### 🎵 Aset Audio
- **Catch Sound Effect**: Pixabay - Collect Points
- **Background Music**: Retrowave by Walen (freetouse.com)

---

Dibuat oleh **Muhammad Akhtar Rizki Ramadha** - 2025
