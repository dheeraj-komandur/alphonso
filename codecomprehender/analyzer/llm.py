# llm.py
from __future__ import annotations
import os
from typing import List, Dict, Any
from dotenv import load_dotenv
import settings
# Load environment variables from .env file
load_dotenv()

from openai import OpenAI
from pydantic import BaseModel

# Only support OpenAI model for now
CHAT_MODEL = settings.CHAT_MODEL
RESEAONING_MODEL = settings.RESEAONING_MODEL
TEMPERATURE = settings.TEMPERATURE

# Always read API key from environment (including .env)
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
if not OPENAI_API_KEY:
    raise ValueError("OPENAI_API_KEY environment variable is not set. Please set it in your .env file or environment variables.")

class LLMClient:
    """Simple wrapper for OpenAI chat completions."""

    def __init__(self):
        self.model = CHAT_MODEL
        self.reseasoning_model = RESEAONING_MODEL
        self.temperature = TEMPERATURE

    def chat(
        self,
        system_prompt: str,
        user_prompt: str,
        history: List[Dict[str, Any]] = None,
        structured_format: type = None
    ) -> str:
        messages = []
        if system_prompt:
            messages.append({
                "role": "system",
                "content": [
                    {
                        "type": "input_text",
                        "text": system_prompt
                    }
                ]
            })
        if history:
            messages.extend(history)
        if user_prompt:
            messages.append({
                "role": "user",
                "content": [
                    {
                        "type": "input_text",
                        "text": user_prompt
                    }
                ]
            })

        client = OpenAI(api_key=OPENAI_API_KEY)
        if structured_format is not None:
            resp = client.responses.parse(
            model=self.model,
            input=messages,
            temperature=self.temperature,
            text_format=structured_format,
            )
        else:
            resp = client.responses.parse(
            model=self.model,
            input=messages,
            temperature=self.temperature,
            )

        # Extract and return only the structured response in JSON, if available
        if hasattr(resp, "output") and resp.output and hasattr(resp.output[0], "content") and resp.output[0].content:
            content = resp.output[0].content[0]
            if hasattr(content, "text"):
                return content.text
        return None
    
    def reason_chat(
        self,
        system_prompt: str,
        user_prompt: str = None,
    ) -> str:
        """
        Similar to chat(), but intended for multi-step reasoning or chain-of-thought prompts.
        Accepts both a system prompt and an optional user prompt.
        """
        messages = []
        if system_prompt:
            messages.append({
                "role": "system",
                "content": [
                    {
                        "type": "input_text",
                        "text": system_prompt
                    }
                ]
            })
        if user_prompt:
            messages.append({
                "role": "user",
                "content": [
                    {
                        "type": "input_text",
                        "text": user_prompt
                    }
                ]
            })

        client = OpenAI(api_key=OPENAI_API_KEY)
        resp = client.responses.create(
            model="o4-mini",
            input=messages,
            reasoning={
                "effort": "high",
                "summary": "auto"
            }
        )

        # Extract and return the output text from the response
        if hasattr(resp, "output") and resp.output:
            for item in resp.output:
                if hasattr(item, "content") and item.content:
                    for content in item.content:
                        if hasattr(content, "text"):
                            return content.text
        return None
  
# if __name__ == "__main__":

#     # class Step(BaseModel):
#     #     explanation: str
#     #     output: str

#     # class MathReasoning(BaseModel):
#     #     steps: list[Step]
#     #     final_answer: str

    

#     # Simple test
#     # system_prompt = "Return comments"
#     # user_prompt = '''<line_number-1>package org.jboss.as.quickstarts.kitchensink;
#     #             <line_number-2>
#     #             <line_number-3>import org.jboss.as.quickstarts.kitchensink.config.ApplicationConfiguration;
#     #             <line_number-4>import org.jboss.as.quickstarts.kitchensink.data.MemberRepository;
#     #             <line_number-5>import org.slf4j.LoggerFactory;
#     #             <line_number-6>import org.springframework.boot.SpringApplication;
#     #             <line_number-7>import org.springframework.boot.autoconfigure.SpringBootApplication;
#     #             <line_number-8>import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
#     #             <line_number-9>import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
#     #             <line_number-10>import org.springframework.context.annotation.Import;
#     #             <line_number-11>import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
#     #             <line_number-12>
#     #             <line_number-13>@SpringBootApplication'''
#     # LLMClient = LLMClient()
#     # response = LLMClient.chat(SYSTEM_PROMPT, user_prompt)
#     # print("Response:", response)
#     LLMClient = LLMClient()
#     SYSTEM_PROMPT = "Answer"
#     response = LLMClient.reason_chat(system_prompt = SYSTEM_PROMPT, user_prompt="What is 4+6 - 7 ornages?")
#     print("Response:", response)
