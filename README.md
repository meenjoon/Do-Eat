<h1 align="center">Do Eat</h1>

<p align="center">
  <a href="https://kotlinlang.org"><img alt="Kotlin Version" src="https://img.shields.io/badge/Kotlin-1.8.0-blueviolet.svg?style=flat"/></a>
  <a href="https://android-arsenal.com/api?level=23"><img alt="API" src="https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://developer.android.com/studio/releases/gradle-plugin"><img alt="AGP" src="https://img.shields.io/badge/AGP-7.3.1-blue?style=flat"/></a>
  <a href="https://docs.aws.amazon.com/ko_kr/AWSEC2/latest/UserGuide/concepts.html"><img alt="Cloud" src="https://img.shields.io/badge/Cloud-Aws EC2-blue.svg"/></a>
</p>

Do Eat 애플리케이션은 지역의 맛집을 찾고 지도에서 확인할 수 있는 기능을 제공합니다.

또한, 해당 맛집에 대한 자세한 정보를 앱 내에서 확인할 수 있도록 도와주며,

모집글 작성과 그룹 채팅 기능을 통해 함께 맛집을 탐방할 동행자를 찾을 수 있어요 ☕️

<p align="center">
  <img src="https://github.com/meenjoon/Do-Eat/assets/88024665/ac2c1466-2381-401b-9622-9088ef0f4928" alt="Do Eat LOGO">
</p>

<a href="https://play.google.com/store/apps/details?id=com.mbj.doeat">
    <img src="http://github.com/PARKJONGMlN/Cours/assets/77707692/ab98934b-b749-4e67-9175-a9eb7faea3d6" width="250" height="100"/>
</a>

> ### **지역 맛집을 함께 방문할 파트너를 찾아주는 ‘Do Eat’입니다.**

- 지역을 검색하여 해당 지역 맛집 정보를 얻을 수 있습니다.
- 원하는 맛집 파티를 생성하고 가입 할 수 있습니다.
- 원하는 맛집 파티를 고를 수 있습니다.
- 함께 맛집을 탐방할 동행자들과 채팅을 할 수 있습니다.


## Tech stack & Open-source libraries

### Android

- Minimum SDK level 24
- [Kotlin](https://kotlinlang.org/) based, [Coroutines](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/) for asynchronous.
- JetPack
  - [Compose](https://developer.android.com/jetpack/compose/mental-model?hl=ko) - Modern Android UI toolkit for building native, declarative user interfaces  
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Store UI related data that isn't destroyed on app rotations.
  - [Navigation](https://developer.android.com/jetpack/compose/navigation?hl=ko) - Handles navigating between your app's destinations.
- [Hilt](https://dagger.dev/hilt/) - Dependency injection.
- [Coil](https://coil-kt.github.io/coil/) - An image loading library for Android backed by Kotlin Coroutines.
- [Retrofit2 & OkHttp3](https://github.com/square/retrofit) - Construct the REST APIs.
- [kotlinx serializable](https://kotlinlang.org/docs/serialization.html#example-json-serialization) - A library in Kotlin that supports object serialization and deserialization

### API Data (Currently Updating)

- Server Development Framework
  - [Spring Boot](https://spring.io/projects/spring-boot)
- Cloud Computing Service
  - [AWS EC2](https://docs.aws.amazon.com/ko_kr/AWSEC2/latest/UserGuide/concepts.html)
- Database
  - [Firebase - Realtime Database](https://firebase.google.com/docs/database?hl=ko)
  - [AWS RDS (MySQL)](https://docs.aws.amazon.com/ko_kr/AmazonRDS/latest/UserGuide/CHAP_MySQL.html)

## Features

> ### Restaurnat_Map & Party_Recruitment & Restaurnat_Parties

<div align="center">

| Shows restaurant locations on a map | Displays a screen for recruiting parties | Lists the available party listings |
| :---------------: | :---------------: | :---------------: |
| <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/restaurant_map.webp" align="center" width="300px"/> | <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/party_recruitment.webp" align="center" width="300px"/> | <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/restaurant_parties.webp" align="center" width="300px"/> |

</div>

> ### Group Chat

<div align="center">

| Displays the detailed view of a group chat room  | Displays my group chat room list. |
| :---------------: | :---------------: |
| <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/group_chatrooms_details.webp" align="center" width="300px"/> | <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/group_chatrooms.webp" align="center" width="300px"/> |

</div>

> ### Login & Setting

<div align="center">

| Login & Auto Login  | Account & Parties |
| :---------------: | :---------------: |
| <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/signin.webp" align="center" width="300px"/> | <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/setting.webp" align="center" width="300px"/> |

</div>

> ### Processing

<div align="center">

| Loading  | Network Disconnected |
| :---------------: | :---------------: |
| <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/loading_processing.webp" align="center" width="300px"/> | <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/network_disconnected_processing.webp" align="center" width="300px"/> |

</div>

> ### Enhancements

<div align="center">

| Realtime Data Processing  | Dark Theme |
| :---------------: | :---------------: |
| <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/realtime_data_processing.webp" align="center" width="300px"/> | <img src="https://github.com/meenjoon/Do-Eat/blob/main/docs/dark_theme.webp" align="center" width="300px"/> |

</div>


## Architecture

Do Eat is based on the MVVM architecture and the Repository pattern.

<p align = 'center'>
<img height = '600' src = 'https://github.com/meenjoon/Upbit/assets/88024665/49226fde-d66f-40d9-aa63-1ed9cd6aa24c'>
</p>

Currently, restaurant data is obtained through direct API calls from web services like Naver.

 We are using two databases:
- For account and post information, we utilize AWS RDS (MySQL) in conjunction with Spring Boot, with a personal AWS EC2 instance for this operation.
- Group chat information is managed using Firebase Realtime Database.

<p align = 'center'>
<img width = '900' src = 'https://github.com/meenjoon/Do-Eat/assets/88024665/70f7106c-4f73-4e4e-bad0-7a28f33abe36'>
</p>

## Design in Figma

![Figma](https://github.com/meenjoon/Do-Eat/assets/88024665/e42f9895-a32b-4a87-90fe-e00b8d5c7c6f)

