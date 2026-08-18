package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.GoalCategory
import com.example.data.model.GoalEntity
import com.example.ui.components.GoalCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleGoal = GoalEntity(
      id = 1,
      title = "Launch new AI project and maintain focus",
      category = GoalCategory.CAREER.displayName,
      whyItMatters = "Achieve creative freedom and master modern AI technology.",
      cachedQuote = "Action conquers doubt. Build today.",
      cachedAuthor = "Thomas Edison",
      cachedStory = "Thomas Edison tested thousands of materials before breakthrough success.",
      cachedTakeaway = "Daily persistence always compounds into mastery.",
      cachedActionNudge = "Complete 25 minutes of deep uninterrupted work now."
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        GoalCard(
          goal = sampleGoal,
          isLoading = false,
          onAchieveGoal = {},
          onRefreshMotivation = {},
          onDeleteGoal = {},
          onTriggerGoalNudge = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

