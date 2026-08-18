package com.example.data.offline

import com.example.data.model.GoalCategory
import com.example.data.model.MotivationContent

object OfflineInspirations {
    private val inspirations = listOf(
        // CAREER & WORK
        MotivationContent(
            quote = "I have not failed. I've just found 10,000 ways that won't work.",
            author = "Thomas Edison",
            story = "Thomas Edison tested over 6,000 plant species before finding carbonized bamboo filaments for the practical electric light bulb. When an assistant asked how he kept working despite thousands of failed attempts, Edison replied that each failure narrowed the field toward the breakthrough.",
            takeaway = "Obstacles and false starts are not setbacks; they are the exact data points that lead directly to your ultimate mastery.",
            actionNudge = "Dedicate the next 25 uninterrupted minutes to the single most difficult task on your plate right now.",
            category = GoalCategory.CAREER.displayName
        ),
        MotivationContent(
            quote = "It is impossible to live without failing at something, unless you live so cautiously that you might as well not have lived at all.",
            author = "J.K. Rowling",
            story = "In 1995, J.K. Rowling was a jobless single mother writing on a manual typewriter in Edinburgh cafes. Her manuscript for Harry Potter was rejected by 12 major publishing houses before a small London publisher finally accepted it on a £1,500 advance.",
            takeaway = "Early rejection and silence do not define your outcome. Relentless persistence transforms unseen work into legendary milestones.",
            actionNudge = "Take one bold action today you have been postponing out of fear of criticism or failure.",
            category = GoalCategory.CAREER.displayName
        ),
        MotivationContent(
            quote = "The people who are crazy enough to think they can change the world are the ones who do.",
            author = "Steve Jobs",
            story = "When Steve Jobs was ousted from Apple in 1985, rather than giving up, he founded NeXT and acquired Pixar, pioneering computer animation. When Apple later acquired NeXT, his comeback led to the iMac, iPod, iPhone, and iPad.",
            takeaway = "Your deepest career setbacks often cultivate the rare creative clarity required for your greatest breakthroughs.",
            actionNudge = "Review your top career objective. Write down the one audacious step that moves the needle today.",
            category = GoalCategory.CAREER.displayName
        ),

        // FITNESS & HEALTH
        MotivationContent(
            quote = "I don't count my sit-ups. I only start counting when it starts hurting because they’re the only ones that count.",
            author = "Muhammad Ali",
            story = "Muhammad Ali used to run miles in heavy combat boots in the early morning fog of Deer Lake, Pennsylvania. He believed championship rounds were won not under the spotlight, but long before in solitary dawn training sessions.",
            takeaway = "True physical transformation happens in the uncomfortable repetitions after your mind asks to stop.",
            actionNudge = "Stand up right now, drink a full glass of water, and do 2 minutes of active stretching or bodyweight squats.",
            category = GoalCategory.FITNESS.displayName
        ),
        MotivationContent(
            quote = "The last three or four reps is what makes the muscle grow. This area of pain divides a champion from someone who is not a champion.",
            author = "Arnold Schwarzenegger",
            story = "Arriving in America with virtually no money and a thick Austrian accent, Arnold trained 5 hours a day while attending business classes and working bricklaying shifts. He refused to let fatigue compromise his vision of becoming the world's greatest bodybuilder.",
            takeaway = "Energy follows discipline, not the other way around. Doing the work when you don't feel like it is the secret weapon.",
            actionNudge = "Commit to your scheduled workout or nutritious meal today without bargaining with hesitation.",
            category = GoalCategory.FITNESS.displayName
        ),
        MotivationContent(
            quote = "I’ve failed over and over and over again in my life. And that is why I succeed.",
            author = "Michael Jordan",
            story = "Cut from his high school varsity basketball team as a sophomore, Michael Jordan went home, locked himself in his room, and wept. The next morning, he began arriving at the gym at 6 AM every day to ensure nobody would ever outwork him again.",
            takeaway = "Disappointment is pure rocket fuel when channeled into daily physical and mental discipline.",
            actionNudge = "Lace up your shoes or prep your fitness gear right now so friction is zero when workout time arrives.",
            category = GoalCategory.FITNESS.displayName
        ),

        // LEARNING & SKILLS
        MotivationContent(
            quote = "Nothing in life is to be feared, it is only to be understood. Now is the time to understand more, so that we may fear less.",
            author = "Marie Curie",
            story = "Marie Curie conducted pioneering radioactive research in a leaky, drafty shed with zero proper laboratory insulation. For four relentless years, she manually stirred boiling caldrons of pitchblende ore to isolate radium, becoming the first person to win two Nobel Prizes.",
            takeaway = "Mastery in complex domains demands patient, focused immersion. Deep understanding is built block by block.",
            actionNudge = "Spend 15 focused minutes studying or practicing your target skill without checking your phone once.",
            category = GoalCategory.LEARNING.displayName
        ),
        MotivationContent(
            quote = "Live as if you were to die tomorrow. Learn as if you were to live forever.",
            author = "Mahatma Gandhi",
            story = "Throughout his lifetime of historic leadership, Gandhi read incessantly across philosophy, law, ethics, and agriculture, translating knowledge into daily ethical experiments and strategic nonviolent action.",
            takeaway = "Continuous learning is the highest leverage investment you can make in your own freedom and impact.",
            actionNudge = "Read one article, chapter, or technical breakdown related to your key goal before the day ends.",
            category = GoalCategory.LEARNING.displayName
        ),

        // MINDSET & GRIT
        MotivationContent(
            quote = "You have power over your mind - not outside events. Realize this, and you will find strength.",
            author = "Marcus Aurelius",
            story = "As Roman Emperor fighting the Antonine Plague and northern border wars, Marcus Aurelius wrote his personal journal 'Meditations' strictly for self-correction, reminding himself daily to reject procrastination, master his impulses, and serve his purpose.",
            takeaway = "You cannot control every external interruption, but you have absolute sovereignty over your own focus and response.",
            actionNudge = "Close unnecessary browser tabs, put your phone in another room, and reclaim mental focus right now.",
            category = GoalCategory.MINDSET.displayName
        ),
        MotivationContent(
            quote = "I may be tired, but I do not stop when I am tired. I stop when I am done.",
            author = "David Goggins",
            story = "Working night shifts spraying cockroaches and weighing nearly 300 pounds, David Goggins looked in the mirror and decided to radically reshape his mindset. Through uncompromising mental toughness, he completed Navy SEAL Hell Week three times and became an ultramarathon legend.",
            takeaway = "When your brain tells you that you are completely exhausted, you have only tapped into roughly 40% of your actual reserve capacity.",
            actionNudge = "Do the exact thing you are currently avoiding for your goal. Start right now for just 5 minutes.",
            category = GoalCategory.MINDSET.displayName
        ),
        MotivationContent(
            quote = "I can be changed by what happens to me. But I refuse to be reduced by it.",
            author = "Maya Angelou",
            story = "Having endured profound childhood trauma and mutism for nearly five years, Maya Angelou channeled her inner voice into literature, civil rights advocacy, and poetry, proving that human resilience can overcome the steepest adversity.",
            takeaway = "Your past delays and struggles are the fertile ground from which your unshakable resolve blooms.",
            actionNudge = "Reframe any self-doubt into a quiet affirmation of your capability and take the next small step forward.",
            category = GoalCategory.MINDSET.displayName
        ),

        // HABIT & LIFESTYLE
        MotivationContent(
            quote = "You do not rise to the level of your goals. You fall to the level of your systems.",
            author = "James Clear",
            story = "After suffering a catastrophic baseball bat injury that broke 30 facial bones and required medically induced comas, James Clear rebuilt his life step-by-step through tiny daily atomic habits, eventually publishing one of the most influential books on habit formation.",
            takeaway = "Achieving your 5 major goals is not about massive occasional heroics, but about small, non-negotiable 1% daily wins.",
            actionNudge = "Pick the smallest 2-minute habit that supports your goal and execute it immediately.",
            category = GoalCategory.HABIT.displayName
        ),
        MotivationContent(
            quote = "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
            author = "Will Durant",
            story = "Legendary Olympic swimmer Michael Phelps swam 365 days a year for over five consecutive years without missing a single Sunday or holiday, building an unmatched competitive advantage one stroke at a time.",
            takeaway = "Consistency creates compounding momentum that talent alone can never replicate.",
            actionNudge = "Protect your habit streak today by completing your baseline goal commitment before relaxation.",
            category = GoalCategory.HABIT.displayName
        ),

        // CREATIVE & PASSION
        MotivationContent(
            quote = "Every child is an artist. The problem is how to remain an artist once he grows up.",
            author = "Pablo Picasso",
            story = "Picasso produced over 50,000 artworks in his lifetime, constantly reinventing his styles across Blue, Rose, Cubist, and Surrealist periods, embracing creative risk and relentless daily studio production into his 90s.",
            takeaway = "Creativity is a muscle developed through prolific daily creation, not waiting for spontaneous inspiration.",
            actionNudge = "Open a blank canvas, document, or project file and create something raw for 10 minutes without judging it.",
            category = GoalCategory.CREATIVE.displayName
        )
    )

    fun getInspirationForGoal(goalTitle: String, categoryName: String): MotivationContent {
        val matching = inspirations.filter { it.category.equals(categoryName, ignoreCase = true) }
        if (matching.isNotEmpty()) {
            return matching.random()
        }
        return inspirations.random()
    }

    fun getRandomIdleNudge(goalTitle: String): MotivationContent {
        val base = inspirations.random()
        return base.copy(
            quote = "Slacking off is a habit; so is excellence. Reclaim this moment.",
            author = "AI Motivator Slump-Buster",
            actionNudge = "Drop the distraction right now and make 5 minutes of progress on '${goalTitle.take(30)}...'",
            isIdleAlert = true
        )
    }

    fun getAllInspirations(): List<MotivationContent> = inspirations
}
