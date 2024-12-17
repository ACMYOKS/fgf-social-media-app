# Social Media App

## App Behavior

The Social Media App provides a better reading experience for the user by managing data both online and offline. Below are the key functionalities and behaviors of the app:

### Data Reading
- **Remote Data Fetching**: The app fetches data from a fake remote data source for posts and post comments.
- **Local Storage**: Remote data is stored in local storage to ensure users can access it even when offline.
- **Eager Data Fetching**: The app continuously attempts to fetch the latest data from the internet whenever a connection is available.

### Data Writing
- **Immediate Updates**: When users interact with the app (e.g., liking a post or adding a comment), the UI updates immediately to reflect these changes.
- **Remote Server Updates**: The app updates the remote server only if there is internet access at the moment the update is made.
- **No Pending Queue**: For simplicity, there is no queue implemented for pending updates.

### User Experience
- Users will see immediate feedback on actions such as liking a post or commenting, while the app ensures that the source of truth (the remote data) is used to refresh the UI later.

This approach provides a responsive user experience while maintaining data integrity across both online and offline states.

### Improvements
- **Always Show Local Resources First**: Users need to wait for the network resources before they can continue browsing, even when the local source is available. Change to get the local data first would improve the UX, but data synchronization and invalidation would be a big challenge.