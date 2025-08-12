import './Utils.css'
import {Flex, Text} from "@radix-ui/themes";

export default function NotFound({text}: Readonly<{ text: string }>) {
    return (
        <Flex className='not-found'>
            <Text size='7' color='gray'>{text}</Text>
        </Flex>
    )
}
