---
  layout: default.md
  title: "Testing guide"
  pageNav: 3
---

# Testing guide

<!-- * Table of Contents -->
<page-nav-print />

<!-- -------------------------------------------------------------------------------------------------------------------- -->

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<box type="info" seamless>

**Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</box>

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`

<!-- TODO: Move this snippet into DG test appendix -->
## Manual undo scenario

1. Add or edit a student, then run `undo`. Confirm that the previous student data is restored and the feedback is
   `Undid last change.`
2. Make two more data changes and run `undo` twice. Confirm that each command restores the next earlier state.
3. Run `find` or `filter`, then make a data change and run `undo`. Confirm that undo shows the complete student list,
   not the earlier filtered result.
4. Continue running `undo` until every earlier state has been restored, then run `undo` once more. Confirm the
   feedback is `Nothing to undo.` and the records do not change.
5. Make a data change, restart TeachAssist, then run `undo`. Confirm the feedback is `Nothing to undo.`

The automated unit and logic tests cover the 100-state history boundary.
<!-- TODO: Move this snippet into DG test appendix -->

--------------------------------------------------------------------------------------------------------------------

## Display settings and window usability

`WindowPlacementTest` checks startup sizing against usable screen bounds, including a 1280 × 720 display at
150% scaling with 60 physical pixels reserved for a taskbar (about 853 × 440 logical pixels).
It also checks restored preferences from larger or disconnected displays, secondary monitors, and minimum sizing.
These geometry tests run without a graphical desktop; they do not change the operating system's display settings.

For release testing, use the following native display settings and leave the taskbar or dock visible:

| Display resolution | Scaling | Approximate logical desktop size before taskbar/dock |
|--------------------|---------|----------------------------------------------------------------------|
| 1280 × 720 | 100% | 1280 × 720 |
| 1280 × 720 | 150% | 853 × 480 |
| 1920 × 1080 | 100% | 1920 × 1080 |
| 1920 × 1080 | 150% | 1280 × 720 |

For each setting:

1. Launch the application with fresh preferences. Check that the entire window, including its title bar and
   bottom status bar, fits inside the usable desktop.
2. Add enough students to require scrolling. Confirm that the command box, command feedback, menu bar, and
   bottom status bar stay visible while the student list scrolls to the last student.
3. Run `add n/Display Check i/Z9999999Z e/display@example.com`, `find n/Display`, and `list`.
   Check that typing and pressing Enter work, feedback is readable, and the list updates.
4. Open Help using `help` and F1 while the command box is focused. Check that the message and Copy URL button
   fit on screen, and that copying the URL works.
5. Resize to the minimum 450 × 400 window. Confirm that the command box and feedback remain visible and at least
   one ordinary student card fits in the scrollable list. Resize larger and repeat the commands.
6. Save a larger window on a larger display, exit, switch to 1280 × 720 at 150%, and relaunch. Confirm that both
   the restored size and position fit the usable desktop. Repeat after disconnecting a secondary monitor.

A rendered layout at the equivalent logical viewport is useful during development. Native release testing is
still needed to check each operating system's window borders, taskbar, font rendering, and scaling behaviour.
