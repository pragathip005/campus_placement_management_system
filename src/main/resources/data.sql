-- Sample data for OA Questions and Interview Experiences

-- Insert companies (make sure they exist in your database first)
-- Assuming companies already exist: Google, Microsoft, Amazon, etc.

-- Insert OA Questions
INSERT INTO oa_questions (company_id, title, description, batch, added_date) VALUES
(1, 'Two Sum (LeetCode)', 'Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target. Example: nums = [2,7,11,15], target = 9 -> [0,1]. Use a HashMap for O(n) solution.', 'Batch 2023', NOW() - INTERVAL '2 days'),
(1, 'Longest Substring Without Repeating Characters', 'Given a string s, find the length of the longest substring without repeating characters. Example: "abcabcbb" -> 3 ("abc"). Use sliding window technique.', 'Batch 2023', NOW() - INTERVAL '5 days'),
(1, 'Median of Two Sorted Arrays', 'Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays. The overall run time complexity should be O(log(m+n)). Use binary search.', 'Batch 2023', NOW() - INTERVAL '7 days'),

(2, 'Reverse Integer', 'Given a signed 32-bit integer x, return x with its digits reversed. If reversing x causes the value to go outside the signed 32-bit integer range [-2^31, 2^31 - 1], then return 0.', 'Batch 2023', NOW() - INTERVAL '3 days'),
(2, 'Container With Most Water', 'You are given an integer array height of length n. There are n vertical lines drawn such that the end points of ith line are (i, 0) and (i, height[i]). Find two lines that together with the x-axis form a container, such that the container contains the most water.', 'Batch 2023', NOW() - INTERVAL '6 days'),

(3, 'Binary Search', 'Given a sorted array of integers and a target value, find the index of the target. If target exists, return its index. Otherwise, return -1. Time complexity should be O(log n).', 'Batch 2023', NOW() - INTERVAL '4 days'),
(3, 'Remove Duplicates from Sorted Array', 'Given an integer array nums sorted in non-decreasing order, remove the duplicates in-place such that each unique element appears only once. Return the number of unique elements.', 'Batch 2023', NOW() - INTERVAL '8 days');

-- Insert Interview Experiences
INSERT INTO interview_experiences (company_id, title, description, applicant_name, experience_date) VALUES
(1, 'Google SWE Interview 2023 - CS Batch', 'Round 1: Two coding questions on arrays and dynamic programming. Round 2: System design for URL shortener with caching and database optimization. Round 3: Behavioral questions about leadership and conflict resolution. Focus on communication and explaining thought process clearly. Practice LeetCode medium problems.', 'Rahul Sharma', NOW() - INTERVAL '10 days'),
(1, 'Google Internship Interview 2024 - CS Batch', 'Technical Round: One medium LeetCode problem (backtracking). Discussion on my previous project. Manager Round: Discussion about team, project goals, and how I can contribute. Very friendly atmosphere. Took 45 minutes total.', 'Priya Gupta', NOW() - INTERVAL '15 days'),

(2, 'Microsoft Experienced Hire Interview', 'Round 1: Coding on merge intervals and graph problems. Round 2: System design - design a notification system. Round 3: HR round focusing on motivation and career goals. Atmosphere was very professional and supportive. Spend good time on system design fundamentals.', 'Aman Singh', NOW() - INTERVAL '12 days'),
(2, 'Microsoft Off-Campus Interview', 'Telephonic round first with HackerRank test. Then virtual had 2 coding rounds focusing on arrays, strings. Final round was HR discussion. Overall duration was 3 hours spread across 2 days. Practice recursion and DP problems.', 'Neha Patel', NOW() - INTERVAL '20 days'),

(3, 'Amazon SDE II Interview - 4 Rounds', 'Round 1 & 2: Two coding problems each, mix of easy-medium difficulty. Round 3: System design bar raiser round focusing on scalability. Round 4: Leadership principles discussion - focus on ownership and customer obsession. Preparation tip: Know AWS services basics.', 'Vikram Joshi', NOW() - INTERVAL '8 days');
