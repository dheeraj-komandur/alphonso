/*
********* AI-Assistant Documentation for - Pair_commented.java *********
The 'Pair.java' file defines a generic class that represents a pair of objects, providing methods to access the individual elements and to compare pairs for equality.
*/

/*
 * Copyright 2022-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.jdbc;

import java.util.Objects;

// (AI Comment) - Represents a generic pair of values, allowing for the storage of two related objects of potentially different types.
public class Pair<L, R> {
    private L left;
    private R right;

    // (AI Comment) - Constructor that initializes the pair with the specified left and right values.
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    // (AI Comment) - Returns the left value of the pair.
    public L left() {
        return left;
    }

    // (AI Comment) - Returns the right value of the pair.
    public R right() {
        return right;
    }

    // (AI Comment) - Checks equality between this pair and another object, returning true if both left and right values are equal.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    // (AI Comment) - Generates a hash code for the pair based on its left and right values.
    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
