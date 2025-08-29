
# Stack Overflow Users

A small but production-grade Android app that lists Stack Overflow users with infinite scrolling, local caching, sorting, and optimistic “follow” toggles. Built with Jetpack Compose, Paging 3 (RemoteMediator), Room, Hilt, Retrofit/Moshi, and Kotlin Coroutines/Flows. Includes a focused unit test suite for the data and presentation layers.


## 🎥 Demo video:

https://github.com/user-attachments/assets/ac609796-a190-4e5a-9458-1e0e754e3a4d

## ✨ Features

- Users feed from Stack Exchange API (/users)

- Infinite scroll with Paging 3 and RemoteMediator

- Offline cache via Room

- Swipe to refresh

- Follow / Unfollow pill with instant UI feedback

- Sorting (Reputation, Creation, Name, Modified)

- Backoff handling for API rate limits (Retry-After / backoff field)

- Error & empty states (initial load, pagination append, no network)

- DI with Hilt

- Unit tests (Repository, RemoteMediator, ViewModel)

## 🧱 Architecture (Clean-ish, Layered)
```text
app/
├─ di/                         # App-wide DI: Retrofit/OkHttp/Moshi/Hilt bindings
├─ navigation/                 # NavHost + Navigation helper composition local
└─ features/
   └─ stackUserList/
      ├─ presentation/         # Compose screens, ViewModel (single-source-of-truth flows)
      ├─ domain/               # Models + Repository interface + enums (sort)
      ├─ data/
      │  ├─ network/           # Retrofit service + Remote data source
      │  ├─ local/             # Room DB, DAO, entities, RemoteKeys, ApiBackoff
      │  ├─ dataMappers/       # DTO <-> Entity <-> Domain mappers
      │  ├─ repository/        # DefaultStackUsersRepository
      │  └─ mediator/          # StackUserInfoMediator (RemoteMediator)
      └─ di/                   # Feature-specific Hilt modules (DB + bindings)
```
## Principles

- Separation of concerns: presentation ↔ domain ↔ data

- Unidirectional data flow: ViewModel exposes Flow<PagingData<...>>

- Mappers isolate API/DB models from domain models

- Testable repositories and mediators, no Android deps in domain/data except Room classes


## 🧩 Tech Stack

 - UI: Jetpack Compose, Material 3, Accompanist SwipeRefresh

- Async: Kotlin Coroutines & Flow

- Paging: Paging 3 (paging-runtime, paging-compose, RemoteMediator)

- DB: Room (Entities/DAO/Transactions)

- Networking: Retrofit, Moshi, OkHttp (logging)

- DI: Hilt

- Images: Coil 3

- Testing: JUnit4, MockK, kotlinx-coroutines-test, paging-testing, Robolectric (for DB tests if needed), Truth


## 🔌 Data Flow & Caching
- RemoteMediator flow

- UI collects Flow<PagingData<StackUserInfoModel>> from Repository.

- Pager uses Room DAO as the pagingSourceFactory + a RemoteMediator:

- REFRESH: determines page based on remote keys (or 1).

- APPEND/PREPEND: reads UserRemoteKeys to decide next/prev page; stops if null.

- Mediator requests GET /users?page=&pagesize=&sort=; writes:

- users table (upsert)

- user_remote_keys table (prev/next)

### DAO join for follow state:
```sql
    SELECT u.*,
           CASE WHEN f.userId IS NULL THEN 0 ELSE 1 END AS isFollowed
    FROM users u
    LEFT JOIN follows f ON f.userId = u.userId
    ORDER BY u.reputation DESC;
```

### Backoff & rate limiting

-  The API may return:

    - backoff field in JSON (seconds)

    - Retry-After header on errors (e.g., 429/400 throttle)

- ApiBackoff stores the “next allowed time”. The mediator delays before next call.

- UI shows a friendly error + retry if throttled.



## 🧠 ViewModel

Exposes:

- usersPagingFromRemote: Flow<PagingData<StackUserInfoModel>> (Pager + RemoteMediator + Room)

sort: MutableStateFlow<StackUserSort> for sorting controls (if/when needed)

- Handles follow/unfollow by writing to the follows table (optimistic UI).



## 📱 UI (Compose)

- StackUserListScreen collects collectAsLazyPagingItems()

- Proper LoadState handling:

  - refresh → initial loading/error/empty

  - append → bottom loading/error footer

- SwipeRefresh calls pagingItems.refresh()

- FollowPill shows the animation difference between “Follow” and “Following”


## 🧪 Testing
What’s covered

- RemoteMediator (StackUserInfoMediator)

  - Handles REFRESH/APPEND/PREPEND decisions

  - Backoff from JSON backoff and Retry-After header

  - Writes to users and remote keys tables correctly

- Repository (DefaultStackUsersRepository)

  - Maps DTO → Domain

  - Pager reading from Room with mapping

  - Follow/unfollow affects joined isFollowed

- ViewModel (StackUsersViewModel)

  - usersPagingFromRemote emits repo data

  - onFollowClick toggles follow state and calls repo
  - 

## Testing notes

- Paging: use paging-testing’s asSnapshot() to turn PagingData into List in tests.

- Coroutines: use StandardTestDispatcher or UnconfinedTestDispatcher with a Main dispatcher rule.

- ViewModels + cachedIn: cancel viewModelScope at the end of the test (or inject a test scope if you add that hook), to avoid UncompletedCoroutinesError.


## 🧯 Troubleshooting

- Endless spinner / no items on first launch
Usually, a rate-limit or network issue. Ensure LoadState.Error is shown and pagingItems.retry() works. Backoff should pause requests and show a message.

- HTTP 429 / 400 (throttle_violation)
Respect Retry-After or backoff. Show a UI explaining the wait and disable refresh until it expires.

- Room error: missing isFollowed
Ensure DAO SELECT includes CASE WHEN f.userId IS NULL THEN 0 ELSE 1 END AS isFollowed.

## 🔍 Design decisions & Trade-offs

- RemoteMediator + Room chosen to provide resilient offline UX and smooth paging.

- Join for isFollowed instead of relation computed fields — efficient and paging-friendly.

- Simple NavigationHelper wrapper to keep Composables clean; can be expanded.

- Hardcoded API key (demo) to reduce setup friction for reviewers. Strongly recommend BuildConfig + Interceptor in real apps.


## 🗺️ Roadmap / Future Work

- Add user detail screen

- Migrate to Eagerly subscribed shared flow for initial fetch if desired

- Retry/backoff countdown UI

- Telemetry + crash reporting

- UI tests with compose-ui-test

- Kover/Jacoco coverage report


## 📄 License

MIT, Apache 2.0, or “All rights reserved” — pick one and paste here.


## Credits

Stack Exchange API © Stack Exchange, Inc. This project is for educational/demo purposes.
