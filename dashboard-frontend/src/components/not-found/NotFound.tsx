import './NotFound.css'
import {Flex, Text} from "@radix-ui/themes";


export default function NotFoundPage() {
    return (
        <div className='not-found-page'>
            <Flex>
                <Text size='9'>404 <Text size='4'> The page you're looking for does not exist.</Text></Text>
            </Flex>
        </div>
    );
}

export function NotFound({text}: Readonly<{ text: string }>) {
    return (
        <Flex align='center' className='not-found-text'>
            <Text size='7' color='gray'>{text}</Text>
        </Flex>
    )
}
