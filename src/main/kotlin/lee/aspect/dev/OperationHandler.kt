/*
 *
 * MIT License
 *
 * Copyright (c) 2022 lee
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lee.aspect.dev

class OperationHandler {

    companion object {
        @JvmStatic
        fun parseTimeStampOperator(input: String): Long {
            var ct: Long = System.currentTimeMillis()

            // check if the string contains "+" or "-"
            if (!input.contains("+") && !input.contains("-")) {
                // if it does not, parse the number from the string and assign it to the original number
                ct = input.toLong()
            } else {
                // parse the number from the string
                val parsedNum = input.replace("\\D".toRegex(), "").toLong()

                // apply the operation to the original number
                if (input.startsWith("+")) {
                    ct += parsedNum
                } else if (input.startsWith("-")) {
                    ct -= parsedNum
                }

            }
            return ct
        }
    }

}